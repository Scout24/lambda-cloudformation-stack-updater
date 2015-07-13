package is24

import com.amazonaws.regions.RegionUtils
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.{UpdateStackResult, Parameter, UpdateStackRequest}
import com.amazonaws.services.lambda.runtime.{LambdaLogger, Context}
import com.amazonaws.services.lambda.runtime.events.SNSEvent
import play.api.libs.json._

import scala.collection.JavaConversions._


case class StackUpdateEvent(stackName: String, region: String, params: Map[String, String])

object StackUpdateEvent {
  implicit val reads = Json.format[StackUpdateEvent]
}


class StackUpdateHandler {

  def handler(event: SNSEvent, context: Context): Unit  = {
    val logger = context.getLogger

    event.getRecords.toList
      .map(_.getSNS.getMessage)
      .map(m => Json.parse(m).validate[StackUpdateEvent])
      .foreach {
        case JsSuccess(StackUpdateEvent(name, region, params), _) =>
          updateStack(name, region, params)
          logger.log(s"update stack $name with params: $params successfully triggered")
        case t =>
          logger.log("error: " + t)
      }
  }

  private def updateStack(stackName: String, region: String, params: Map[String, String]) = {
    val cloudFormation = new AmazonCloudFormationClient().withRegion[AmazonCloudFormationClient](RegionUtils.getRegion(region))
    cloudFormation.updateStack(new UpdateStackRequest()
      .withStackName(stackName)
      .withUsePreviousTemplate(true)
      .withCapabilities("CAPABILITY_IAM")
      .withParameters(params.map{case (key, value) => new Parameter().withParameterKey(key).withParameterValue(value)}))
  }

}


