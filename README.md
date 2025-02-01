# clojure-practice


```bash
$ docker pull clojure:latest
$ docker run -it -v $(pwd):/app -w /app clojure /bin/bash
```

## Create project

```bash
$ cd /tmp
$ lein new app clojure-practice
$ cp -r clojure-practice/* /app/
```


## Run

```bash
$ cd /app
$ lein run
```

