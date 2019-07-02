# NodeJs workshop for Kafka

Exposes some usages for Kafka in NodeJs.

Every variable that you might want to change are in the `src/index.ts` for a basic usage.

The other code files contains working snippets that are available to you in order to extend the possibilities and discover more in depth the wonderful world of kafka.

It really is recommended to use under `docker`:
```bash
docker build -t kafka-node .
docker run kafka-node
```

Otherwise, you'll have to change the path in the `index.ts` for the certificates to match your system install.