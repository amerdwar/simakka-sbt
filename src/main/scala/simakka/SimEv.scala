package simakka

/**
  * 
  */

sealed trait SimEvent {
  var time: Double
}

case class SimEv(var time: Double,var tag: Int,var from: Int,var to: Int,var data: Option[String]) extends SimEvent

case class NullMessage(var time: Double, from: Int, to: Int) extends SimEvent


final object SimEvNone extends SimEvent {
  override var time: Double = Double.MaxValue
}

case class cpuMessage(time: Double, tag: Int, from: Int, to: Int, data: Option[String]) extends
     SimEv(time: Double, tag: Int, from: Int, to: Int, data: Option[String]) {

  var totalTime:Double=0;
   var times:Int=0;
  var deltaTime:Double=0.6;

  def dectimeAndGet():Double ={

    times += 1
    if (totalTime >=
      deltaTime) {
      totalTime=totalTime- deltaTime
      deltaTime
    }
    else {
      val last = deltaTime
      totalTime = 0
      last
    }
  }

  def hasNext():Boolean={
     return totalTime>0
  }
}

