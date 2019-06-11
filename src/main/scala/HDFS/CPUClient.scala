package HDFS

import simakka.{SimEntity, SimEv, cpuMessage}
import simakka.distributions.SimRndUniform
import HDFS.SourceTags

import scala.collection.mutable.ArrayBuffer


class CPUClient(localName: String, localId: Int) extends SimEntity(localName , localId) with SourceTags{

  var MIN_ARRIVAL_TIME=0.0;
  var MAX_ARRIVAL_TIME=0.0;
  var lastSentTime=0.0;
  var a=1;
  var randArival:SimRndUniform=null;
  var serverName="";
  var serverTag:Int=0;
  override def handleEvent(ev: SimEv): Unit = {

    ev.tag match {
      case TAG_START_SOURCE => {
        //simTime=simTime+s;
        var s=0.0;
        s = randArival.sample();
        var message:cpuMessage=new cpuMessage(s+simTime, engine_check, this.localID,serverTag,null);
        message.totalTime=200;
        message.deltaTime=0.6;
        lastSentTime=s;
        schedule(s,message );


        if( a < 12){
          a+=1;

          s = randArival.sample();
          scheduleLocal(s+lastSentTime,TAG_START_SOURCE, null)//send customer to client
          println("***********schedule ***************"+localName)
          if(a==11){
            println("***********source statistics***************")
          }
        }
      }
    }
  }

  override def initEntity(data: Option[ArrayBuffer[String]]): Unit = {
    //to do extraxt data from data array

    serverName=data.get(0);

    serverTag= getId(serverName).get;
    MIN_ARRIVAL_TIME=4.0;
    MAX_ARRIVAL_TIME=10.0;

    randArival=  new SimRndUniform("arrival",MIN_ARRIVAL_TIME,MAX_ARRIVAL_TIME);
randArival.setSeed(System.nanoTime())
    var s=randArival.sample();
    scheduleLocal(s,TAG_START_SOURCE, null)//send customer to client

    tick()
    log.info("init is source");



  }


}
