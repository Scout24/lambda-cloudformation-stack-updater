package is24

import com.amazonaws.regions.RegionUtils
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.{DescribeStacksRequest, UpdateStackResult, Parameter, UpdateStackRequest}
import com.amazonaws.services.lambda.runtime.{LambdaLogger, Context}
import com.amazonaws.services.lambda.runtime.events.SNSEvent
import play.api.libs.json._

import scala.collection.JavaConversions._


case class StackUpdateEvent(stackName: String, region: String, notificationARN: String, params: Map[String, String])

object StackUpdateEvent {
  implicit val reads = Json.format[StackUpdateEvent]
}


class StackUpdateHandler {

  def handler(event: SNSEvent, context: Context): Unit  = {
    val logger: LambdaLogger = context.getLogger
    event.getRecords.toList
      .map(_.getSNS.getMessage)
      .map{p => logger.log(s"payload: $p");p}
      .map(m => Json.parse(m).validate[StackUpdateEvent])
      .foreach {
        case JsSuccess(StackUpdateEvent(name, region, notificationARN, params), _) =>
          updateStack(logger, name, region, notificationARN, params)
          logger.log(s"update stack $name with params: $params successfully triggered")
        case t =>
          logger.log("error: " + t)
      }
  }

  private def updateStack(logger: LambdaLogger, stackName: String, region: String, notificationARN: String, params: Map[String, String]) = {
    val cloudFormation = new AmazonCloudFormationClient().withRegion[AmazonCloudFormationClient](RegionUtils.getRegion(region))

    val result = cloudFormation.describeStacks(new DescribeStacksRequest().withStackName(stackName))

    val stack = result.getStacks.toList.find(_.getStackName == stackName).get

    val existingParameters = stack.getParameters.toList.map(p => (p.getParameterKey, p.getParameterValue)).toMap

    val resultingParameters = existingParameters ++ params

    logger.log(s"resultingParameters: $resultingParameters")

    cloudFormation.updateStack(new UpdateStackRequest()
      .withStackName(stackName)
      .withNotificationARNs(notificationARN)
      .withUsePreviousTemplate(true)
      .withCapabilities("CAPABILITY_IAM")
      .withParameters(resultingParameters.map{case (key, value) => new Parameter().withParameterKey(key).withParameterValue(value)}))
  }

}


