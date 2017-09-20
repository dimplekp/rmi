FROM myubuntu:1

COPY ./rmi-lib/target/client-runnable.jar /data/client-runnable.jar

CMD java -cp /data/client-runnable.jar mytest.PingPongClient

