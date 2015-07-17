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
Usage
=====


TODOs
=====


License
=======
The Lambda CloudFormation Stack Updater is licensed under [Apache License, Version 2.0](https://github.com/ImmobilienScout24/lambda-cloudformation-stack-updater/blob/master/LICENSE).



# send notification

    aws sns publish --topic-arn arn:aws:sns:eu-west-1:744969810879:stackUpdateMessages \
        --message "{ \"stackName\": \"perf-6\", \"region\": \"eu-central-1\", \"params\": { \"dockerImageVersion\": \"$version\"}}"
