# KSQL workshop

You'll find the associated `launcher.sh` script which bootstraps the docker environment,you just have to change the volume mounting path for the certs depending on your system installation.
It takes a bunch of seconds to start up completely.

You can tail the logs using `docker logs ksql-server -f`.

You then simply have to run the following command to enter the `KSQL-cli` console:
```bash
docker run -it --network kafka confluentinc/cp-ksql-cli http://ksql-server:8088 
```

And voilà, you're in, just type `show topics;` for a starter.

