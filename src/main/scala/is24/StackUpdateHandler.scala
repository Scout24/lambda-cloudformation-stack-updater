package is24

import com.amazonaws.regions.RegionUtils
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient
import com.amazonaws.services.cloudformation.model._
import com.amazonaws.services.lambda.runtime.events.SNSEvent
import com.amazonaws.services.lambda.runtime.{Context, LambdaLogger}
import play.api.libs.json._

import scala.collection.JavaConversions._


case class StackUpdateEvent(stackName: String, region: String, notificationARN: String, params: Map[String, String])

object StackUpdateEvent {
  implicit val reads = Json.format[StackUpdateEvent]
}

case class ParameterKey(value: String)

case class ParameterValue(value: String)

class StackUpdateHandler {

  import StackUpdateHandler._

  def handler(event: SNSEvent, context: Context): Unit = {
    val logger: LambdaLogger = context.getLogger
    event.getRecords.toList
      .map(_.getSNS.getMessage)
      .map { p => logger.log(s"payload: $p"); p }
      .map(m => Json.parse(m).validate[StackUpdateEvent])
      .foreach {
      case JsSuccess(StackUpdateEvent(name, region, notificationARN, params), _) =>
        val cloudFormation = new AmazonCloudFormationClient().withRegion[AmazonCloudFormationClient](RegionUtils.getRegion(region))
        val stack = findStack(cloudFormation, name)
        val noEchos = noEchoFromStackName(cloudFormation, name)
        val preparedParams = prepareStackParams(existingParameters(stack), noEchos.toSet, params.map { case (k, v) => (ParameterKey(k), ParameterValue(v)) })

        logger.log(s"prepared params: $preparedParams")

        cloudFormation.updateStack(new UpdateStackRequest()
          .withStackName(name)
          .withNotificationARNs(notificationARN)
          .withUsePreviousTemplate(true)
          .withCapabilities("CAPABILITY_IAM")
          .withParameters(preparedParams))
        logger.log(s"update stack $name with params: $params successfully triggered")
      case t =>
        logger.log("error: " + t)
    }
  }


}

object StackUpdateHandler {
  
  def prepareStackParams(existingParams: Map[ParameterKey, ParameterValue], noEchoKeys: Set[ParameterKey],  newParams: Map[ParameterKey, ParameterValue]) = {
    val noEchoParams = noEchoKeys -- newParams.keySet
    val resultingParametersWithoutEcho = existingParams.filterNot { case (key, _) => noEchoKeys(key) } ++ newParams

    (resultingParametersWithoutEcho.map {
      case (key, value) => new Parameter().withParameterKey(key.value).withParameterValue(value.value)
    } ++
      noEchoParams.map(k => new Parameter().withParameterKey(k.value).withUsePreviousValue(true))).toSet
  }

  def existingParameters(stack: Stack) = stack.getParameters.toList.map(p => (ParameterKey(p.getParameterKey), ParameterValue(p.getParameterValue))).toMap

  def findStack(cloudFormation: AmazonCloudFormationClient, stackName: String) = {
    val result = cloudFormation.describeStacks(new DescribeStacksRequest().withStackName(stackName))
    result.getStacks.toList.find(_.getStackName == stackName).get
  }

  def noEchoFromStackName(cloudFormation: AmazonCloudFormationClient, stackName: String) = {
    val summary = cloudFormation.getTemplateSummary(new GetTemplateSummaryRequest().withStackName(stackName))
    summary.getParameters.toList.filter(_.getNoEcho).map(p => ParameterKey(p.getParameterKey))
  }

}


