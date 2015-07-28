# Lambda CloudFormation Stack Updater
Cross-account stack updates through a minimalistic interface and without "permit everything" policies.
Intended to be used with [AWS deployment notifier](https://github.com/ImmobilienScout24/aws-deployment-notifier).

Intention
=========
In larger AWS setups you often work with dedicated accounts for different teams, products and/or stages.
Deploying code to these accounts poses the challenge that you either have to build artefacts within the account
or deploy accross account boundaries.

Architecture
============
                                                   │

                  CLD Account                      │                      Team Account

                                                   │
                                                     SNS with
                    ┌──────────┐                   │ resource
                    │          │  stackName: app      policy
    Build 35 ─────▶ │          │   version: 35  ┌──┴──────────────────┐           ┌───────────┐
                    │          │   ───────────▶ │ deployment msgs     │──────────▶│  ....     │
                    │          │                └──┬──────────────────┘           │     .     │
                    │    D     │                                                  │     ..    │
                    │    e     │                   │                   Validation │    ...    │
                    │    p     │                                                  │   .. ..   │
                    │    l     │                   │                 IAM policies │  ..    .  │
                    │    o     │                                                  │           │
                    │    y     │                   │                              └───────────┘
                    │    m     │                                                        │
                    │    e     │                   │          updateStack(app, params:  │
                    │    n     │                                       { version: 35 }) │
                    │    t     │                   │                                    │
                    │          │                                                        ▼
                    │    N     │                   │                         ┌────────────────┐
                    │    o     │                                             │                │
                    │    t     │                   │                         │ CloudFormation │
                    │    i     │                                             │                │
                    │    f     │                   │                         └────────────────┘
                    │    i     │                                                        │
                    │    e     │                   │  SQS with                          │
                    │    r     │                      resource                          │
                    │          │ getMessages()     │   policy                           ▼  SNS
                    │          │ ──────────────▶┌─────────────────────┐      ┌────────────────┐
                    │          │                │ stack update events │◀─────│   update events│
                    │          │ ◀──────────────└─────────────────────┘      └────────────────┘
                    │          │  UPDATE_COMPLETE  │
                    └──────────┘
                                                   │

                                                   │
Setting Up
==========
The Lambda function is available as ready-to-use package at:
    
    s3://de.is24.val.update-stack-function/update-stack-function-1.0.3.jar

In addition to that, there is a CloudFormation template `src/main/cfn/deployment-api.json` which sets up all the
resources needed in your team account. Because CloudFormation is currently not able to grant SNS permissions to invoke
the Lambda function this is implemented in `bin/create-deployment-stack.sh`:
 
    $ bin/create-deployment-stack.sh STACKNAME update-stack-function-1.0.3.jar

Now your deployment API is ready to use!

Interface
=========
The function expects input messages like this:

    {
        "stackName": "performance",
        "notificationARN": "arn:aws:sns:eu-west-1:744969810879:deployment-api-test-resultMessages",
        "region": "eu-west-1",
        "params": {
            "dockerImageVersion": "69"
        }
    }

* `stackName`: CloudFormation stack to update
* `notificationARN`: SNS topic to send CloudFormation events to, was created with the template, ARN is available as
  output parameter `resultTopic`
* `region`: where to use CloudFormation
* `params`: stack parameters to update, not provided parameters will not be changed

Resulting CloudFormation events are streamed to the SQS queue that was created by the template its ARN is available as
output parameter `resultQueue`.

Usage
=====
**We strongly suggest to use [AWS deployment notifier](https://github.com/ImmobilienScout24/aws-deployment-notifier)
which builds input messages and parses resulting events for you.**

In case you prefer to do this by yourself here is an example how to send messages using the AWS cli:

    aws sns publish --topic-arn arn:aws:sns:eu-west-1:744969810879:deployment-api-test \
        --message "{\"stackName\": \"performance\", \"notificationARN\": \"arn:aws:sns:eu-west-1:744969810879:deployment-api-test-resultMessages\", \"region\": \"eu-west-1\", \"params\": { \"dockerImageVersion\": \"69\"}}"

TODOs
=====
* Capsulate CloudFormation specific update messages in the Lambda function, provide a stable and well defined
  interface to the client
* Provide feedback to the client in case of CloudFormation errors.

License
=======
The Lambda CloudFormation Stack Updater is licensed under [Apache License, Version 2.0](https://github.com/ImmobilienScout24/lambda-cloudformation-stack-updater/blob/master/LICENSE).
