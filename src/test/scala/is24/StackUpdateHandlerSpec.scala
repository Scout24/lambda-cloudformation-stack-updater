package is24

import com.amazonaws.services.cloudformation.model.Parameter
import org.scalatest.{Matchers, FlatSpec}

class StackUpdateHandlerSpec extends FlatSpec with Matchers {



  "StackUpdater" should "assemble params" in {
    val existingparams: Map[ParameterKey, ParameterValue] = Map(
      ParameterKey("testKeyExisting1") -> ParameterValue("testValueExisting1"),
      ParameterKey("testKeyExisting2") -> ParameterValue("testValueExisting2")
    )

    val noEchoKeys: Set[ParameterKey] = Set(ParameterKey("testKeyExisting3"))
    val newParams: Map[ParameterKey, ParameterValue] = Map(
      ParameterKey("testKeyExisting4") -> ParameterValue("newTestValueExisting4")
    )

    val preparedParams = StackUpdateHandler.prepareStackParams(existingparams, noEchoKeys, newParams)

    preparedParams should contain(new Parameter().withParameterKey("testKeyExisting1").withParameterValue("testValueExisting1"))
    preparedParams should contain(new Parameter().withParameterKey("testKeyExisting2").withParameterValue("testValueExisting2"))
    preparedParams should contain(new Parameter().withParameterKey("testKeyExisting3").withUsePreviousValue(true))
    preparedParams should contain(new Parameter().withParameterKey("testKeyExisting4").withParameterValue("newTestValueExisting4"))
  }

  it should "overwrite noEcho params with new params" in {
    val noEchoKeys: Set[ParameterKey] = Set(ParameterKey("testKeyExisting1"))
    val newParams: Map[ParameterKey, ParameterValue] = Map( ParameterKey("testKeyExisting1") -> ParameterValue("otherTestValueExisting1"))
    val preparedParams = StackUpdateHandler.prepareStackParams(Map(), Set(), newParams)
    preparedParams should contain(new Parameter().withParameterKey("testKeyExisting1").withParameterValue("otherTestValueExisting1"))
  }

  it should "use previous values for no echo params" in {
    val existingparams: Map[ParameterKey, ParameterValue] = Map(ParameterKey("testKeyExisting1") -> ParameterValue("****"))
    val noEchoKeys: Set[ParameterKey] = Set(ParameterKey("testKeyExisting1"))
    val preparedParams = StackUpdateHandler.prepareStackParams(existingparams, noEchoKeys, Map())
    preparedParams should contain(new Parameter().withParameterKey("testKeyExisting1").withUsePreviousValue(true))
  }

  it should "use overwrite existing params with new params" in {
    val existingparams: Map[ParameterKey, ParameterValue] = Map(ParameterKey("testKeyExisting1") -> ParameterValue("newTestValueExisting1"))
    val newParams: Map[ParameterKey, ParameterValue] = Map( ParameterKey("testKeyExisting1") -> ParameterValue("otherTestValueExisting1"))
    val preparedParams = StackUpdateHandler.prepareStackParams(existingparams, Set(), newParams)
    preparedParams should contain(new Parameter().withParameterKey("testKeyExisting1").withParameterValue("otherTestValueExisting1"))
  }
}
