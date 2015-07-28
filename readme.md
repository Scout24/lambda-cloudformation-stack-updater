# Lambda CloudFormation Stack Updater

Cross-account stack updates through a minimalistic interface and without "permit everything" policies.
Intended to be used with [AWS deployment notifier](https://github.com/ImmobilienScout24/aws-deployment-notifier).

Intention
=========

In larger AWS setups you often work with dedicated accounts for different teams, products and/or stages.
Deploying code to these accounts poses the challenge that you either have to build within the account
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

The Lambda function is available as ready-to-use Lambda package at:
    
    s3://de.is24.val.update-stack-function/update-stack-function-1.0.3.jar

In addition to that, there is a CloudFormation template which sets up all the resources needed in your team account.
Because CloudFormation is currently not able to grant SNS permissions to invoke the Lambda function this is implemented
in `bin/create-deployment-stack.sh`:
 
    $ bin/create-deployment-stack.sh STACKNAME update-stack-function-1.0.3.jar

Now your deployment API is ready to use.

Usage
=====

In order to provide the function with all the necessary data the client needs to parametrized:

* SNS input topic
* 

The values are available as output parameters of the stack that was created during setup.


TODOs
=====


License
=======
The Lambda CloudFormation Stack Updater is licensed under [Apache License, Version 2.0](https://github.com/ImmobilienScout24/lambda-cloudformation-stack-updater/blob/master/LICENSE).



# send notification

    aws sns publish --topic-arn arn:aws:sns:eu-west-1:744969810879:deployment-api-test4 \
        --message "{\"stackName\": \"performance\", \"notificationARN\": \"arn:aws:sns:eu-west-1:744969810879:deployment-api-test4-resultMessages\", \"region\": \"eu-west-1\", \"params\": { \"dockerImageVersion\": \"69\"}}"
