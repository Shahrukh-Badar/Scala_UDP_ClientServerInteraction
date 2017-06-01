/**
  * Created by Shah on 5/10/2017.
  */
object myClasses {

  abstract sealed class MsgType

  case class Msg(message: String) extends MsgType

  case class End() extends MsgType

  /*{
     def getClassName(): String = {
       return this.getClass().getName()
     }
   }*/

}


