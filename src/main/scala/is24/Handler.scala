package is24

import com.amazonaws.regions.Region._
import com.amazonaws.regions.Regions._
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model.{Parameter, UpdateStackRequest}
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.SNSEvent
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord
import play.api.libs.json._
import scala.collection.JavaConversions._


case class StackUpdateEvent(stackName: String, version: String)

object StackUpdateEvent {
  implicit val reads = Json.format[StackUpdateEvent]
}


class Handler {

  def myHandler(event: SNSEvent, context: Context): String  = {
    val logger = context.getLogger

    val records: List[SNSRecord] = event.getRecords.toList
    records.foreach { r =>
      logger.log(" r.getEventSource : " +  r.getEventSource)
      logger.log(" r.getSNS.getMessage : " +  r.getSNS.getMessage)
      Json.parse(r.getSNS.getMessage).validate[StackUpdateEvent] match {
        case JsSuccess(StackUpdateEvent(stackName, version), _) =>
          logger.log(s"got update for stack: ${stackName} with ${version}")
          val cf = new AmazonCloudFormationClient()
          cf.setRegion(getRegion(EU_CENTRAL_1))
          val r = new UpdateStackRequest()
          r.setStackName(stackName)
          r.setUsePreviousTemplate(true)
          r.setCapabilities(Seq("CAPABILITY_IAM"))

          r.setParameters(Seq(new Parameter().withParameterKey("dockerImageVersion").withParameterValue(version)))
          val result = cf.updateStack(r)
        case t => logger.log("error: " + t)
      }


    }

    "Hello"
  }

}


