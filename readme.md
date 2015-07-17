# Lambda CloudFormation Stack Updater

Lambda function the triggers CloudFormation stack updates and connects the resulting events with an SQS queue

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
    Build 35   ───────▶  │          │   version: 35  ┌──┴──────────────────┐           ┌───────────┐
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
                         │          │                   │
                         └──────────┘
                                                        │
    
                                                        │

Usage
=====


TODOs
=====


License
=======
The Lambda CloudFormation Stack Updater is licensed under Apache License, Version 2.0



# send notification

    aws sns publish --topic-arn arn:aws:sns:eu-west-1:744969810879:stackUpdateMessages \
        --message "{ \"stackName\": \"perf-6\", \"region\": \"eu-central-1\", \"params\": { \"dockerImageVersion\": \"$version\"}}"
