# JJUG-CCC-2019-Spring

## OpenCensusで始める分散トレーシングと監視

[Abstract](http://www.java-users.jp/ccc2019spring/#/sessions/8b420ee7-94a7-4f5c-9db0-7798f66d05c8)

[slide]()

### requirements

#### development

- Java 8
- docker
- docker-compose

#### deployment

- Google App Engine
- Stackdriver
    - Logging
    - Monitoring
    - Trace


### setup

```bash
make jib
```

### run

only app

```bash
make start
```

### demo

```bash
make up
```

```bash
make down
```
