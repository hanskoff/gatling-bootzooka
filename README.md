
## Run performance tests
  
  ./gradlew -Dserver.port=8181 loadTest

## Gatling fat-jar
Fat-jar:

    ./gradlew shadowJar

    cd build/libs

    mkdir -p target/test-classes
Ivoke:

    java -XX:+UseThreadPriorities -XX:ThreadPriorityPolicy=42 -Xms512M -Xmx512M -Xmn100M \
    -XX:+HeapDumpOnOutOfMemoryError -XX:+AggressiveOpts -XX:+OptimizeStringConcat \
    -XX:+UseFastAccessorMethods -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled \
    -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false \
    -cp ScalaForGroovyGradle-1.0-SNAPSHOT-all.jar: io.gatling.app.Gatling -s pl.jan.Sample07QuickGatling