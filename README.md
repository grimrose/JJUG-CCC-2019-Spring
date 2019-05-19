# JJUG-CCC-2019-Spring

## OpenCensusで始める分散トレーシングと監視

[Abstract](http://www.java-users.jp/ccc2019spring/#/sessions/8b420ee7-94a7-4f5c-9db0-7798f66d05c8)

[slide](https://docs.google.com/presentation/d/e/2PACX-1vRotoqhMthVJ6fsAnYIAz04M_-W2HFG43Hc88IXRjlx2WI7z9HB6dGJyj6KhRv-iryz-FD5kxyA0vCr/pub)

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
