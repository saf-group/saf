#!/bin/bash

source /etc/profile
dir="/data/inc/logs/$HOSTNAME"
mkdir -p ${dir}
cd `dirname $0`
JAVA_BIN=/app/3rd/jdk/default/bin/java
APP_HOME=`pwd`
PROC=${APP_HOME##*\/}
LIB_DIR="${APP_HOME}/${PROC}"
LIB_JARS=`ls ${APP_HOME}/*.jar`
JAVA_OPTS="${JAVA_OPTIONS} ${sentinel_server} -verbose:gc -Xloggc:${dir}/gc.log  -Dapp.logging.path=${dir}  -Dcsp.sentinel.log.dir=${dir}"


usage() 
{
    echo "sh run.sh [start|stop|restart|status]";
}

waiting() {
for((k=1;k<=3;k++));
do
    echo -ne "."
    sleep 1
done
echo ""
}

checkpid(){
    pid=$(ps  aux | grep "$APP_HOME/" |grep java | grep -v grep| awk -F " " '{print $2}')
}


status() {
    checkpid
    if [  x"$pid" != x  ];then
        echo "$PROC is Started pid:$pid";
        return 0;
    else
        echo "$PROC is Stopped"
        return 255;
    fi
}


stop() {
    checkpid
    if [  x"$pid" = x  ];then
        echo "${PROC} is Stopped"
    else
        rm  -rf /tmp/readness
        kill -15  $pid
        echo -ne "${PROC} Stopping "
        waiting
        checkpid
        if [  x"$pid" != x  ];then
            kill -9 $pid
            echo  "${PROC} Stop   Sucessfully !!!"
        else
            echo  "${PROC} Stop   Sucessfully !!!"
        fi
    fi  
}


start() {
    checkpid
    if [  x"$pid" != x  ];then
        echo "${PROC} is Started pid:$pid"
    else
        #nohup ${JAVA_BIN} ${JAVA_OPTS}  -Denv=${denv} -Dapp.id=${appid} ${DEBUG} -jar ${LIB_JARS} > /${dir}/stdout.log 2>&1 &
       exec ${JAVA_BIN} ${JAVA_OPTS}  -Denv=${denv} -Dapp.id=${appid} ${DEBUG} -jar ${LIB_JARS} 
        echo -ne "$PROC Starting " 
        waiting
        checkpid
        if [ x"$pid" = x ];then
            echo  "$PROC Staring   Fail  !!!"
        else 
            echo  "${PROC} Start  Sucessfully !!!"
        fi
       sleep 60
       touch /tmp/readness
    fi
}


restart() {
    stop;
    start;
    
}


if [ $# -lt 1 ];then
    usage;
    exit 1;
fi

case $1 in
    stop) stop;;
    start) start;;
    restart) restart;;
    status) status;;
    *) usage;;
esac
