package HDFS

import simakka.{SimEntity, SimEv,cpuMessage}
import simakka.distributions.SimRndUniform
import scala.collection.mutable.Queue

import scala.collection.mutable.ArrayBuffer

trait SourceTags{
  final val engine_add=1
  final val engine_check=2
  final val TAG_START_SOURCE= 3
}

class CPU( localName: String,  localId: Int) extends SimEntity(localName , localId) with SourceTags{
  var q=Queue[cpuMessage]();
  var numCore=0;
  var NUM_CUSTOMERS_SERVED=0.0;
  var MIN_SERVICE_TIME=2.0;
  var MAX_SERVICE_TIME=3.0;
  var NUM_LIMITE=20000;

  var i=0;
  var startTime=0.0;
  var endTime=0.0;
  //define variable of statistics
  var numDelayedCustomers=0.0;

  var numInQ=0.0;
  var totalDelays=0.0;
  var delay=0.6;
  var timeLastEvent=0.0;
  var areaNumInQ = 0.0;
  var areaServerStatus=0.0;
 var freeCores=0;



  val queue = scala.collection.mutable.Queue[Double]()

  override def initEntity(data: Option[ArrayBuffer[String]]): Unit = {

    numCore=4;
    freeCores=numCore;

    log.info("init server done");

    statMan.addMeasure(statMan.STAT_USER , "totalDelays")
    statMan.addMeasure(statMan.STAT_USER , "averageDelays")
    statMan.addMeasure(statMan.STAT_USER , "serverUtil")
    statMan.addMeasure(statMan.STAT_USER , "averageQlen")


  }
  override def handleEvent(ev1: SimEv): Unit = {
println(numDelayedCustomers+"     uuuuuuuuu");
    if(numDelayedCustomers>=77.0)
      endSimAndPrint()
    updateStat;
    var ev=ev1.asInstanceOf[cpuMessage]
    println(" total time "+ev.totalTime);
    ev.tag match {

      case `engine_check` => {
        ev.tag = engine_add;
        println("engine check"+ev.tag);
        if (numDelayedCustomers == 0.0) {
          startTime = System.nanoTime();

        }
        if (freeCores == 0) {
          println("free core no")
          numInQ += 1;
          q += ev;
        } else {
          //there is free core
          delay = 0; //there is no delay because the customer has been served im
          totalDelays += delay;
          statMan.updateMeasure("totalDelays", 0);
          i = i + 1;
          if (ev.hasNext()) {
            println("free core has next"+ev.totalTime)
            freeCores -= 1;
            var slice = ev.dectimeAndGet();
            schedule(slice, ev);


          }
        }
      }
      case `engine_add` =>{
        //println("Dep at "+ev.time+" num cust ser "+numDelayedCustomers)

println("engine add");
        if(!ev.hasNext()){

print("  ppppppppppppppp ")
          numDelayedCustomers=numDelayedCustomers+1.0;

          if(q.length==0)
            freeCores+=1;
          else
            {
            var cputemp=q. dequeue();
              var slice=cputemp.dectimeAndGet();
              schedule(slice,cputemp);

            }

        }else{
            if(q.length>0){
              q.enqueue(ev);
              var cputemp=q.dequeue();
              var slice=cputemp.dectimeAndGet();
              schedule(slice,cputemp);
            }else{
              totalDelays=simTime-ev.time;
              var slice=ev.dectimeAndGet();
              println("   ssssss   "+slice)
              schedule(slice,ev);
            }


        }

      }
    }

  }

  def updateStat():Unit = {
    var timeSinceLastE = 0.0;
    timeSinceLastE = simTime-timeLastEvent;//here we calculate the time slide
    timeLastEvent =  simTime;
    areaNumInQ += numInQ*timeSinceLastE;
    areaServerStatus =areaServerStatus+ (((numCore - freeCores) * timeSinceLastE) / numCore)

  }
  def endSimAndPrint()={

    statMan.updateMeasure("totalDelays", totalDelays)
    statMan.updateMeasure("averageDelays", totalDelays/numDelayedCustomers)
    statMan.updateMeasure("averageQlen", areaNumInQ/simTime)
    statMan.updateMeasure("serverUtil", areaServerStatus/simTime)


    endTime=System.nanoTime();

    endTime=(endTime-startTime)/1000000;
    println
    println("the time is "+endTime)


    endSimulation

  }


}