/**
  * Created by Shah on 5/10/2017.
  */

import java.net.DatagramSocket
import java.net.{DatagramPacket, InetAddress}
import myClasses.{End, Msg, MsgType}
import com.twitter.chill.ScalaKryoInstantiator
import com.esotericsoftware.kryo.io.{Input, Output}


class _myClient01(_mesg: String) extends Runnable {
  var myMesg: String = _mesg
  var myRes: String = ""

  def getServerRes(): String = {
    return myRes
  }


  def run(): Unit = {
    val dataSocket: DatagramSocket = new DatagramSocket();
    try {
      println("Client 01 is sending: " + _mesg)
      var clientInput: String = "";
      //do {
      //println("Client 01:> Please enter messege below. (Press Enter key to send and to terminate send 'end' as a message.)")
      clientInput = this.myMesg //scala.io.StdIn.readLine().trim();
      val instantiator = new ScalaKryoInstantiator
      instantiator.setRegistrationRequired(false)
      val kryo = instantiator.newKryo()
      val output = new Output(1024)
      val host: InetAddress = InetAddress.getLocalHost()
      val port: Int = 50000

      if (clientInput.toLowerCase() != "end") {
        var sendMsgObj = new Msg(clientInput)
        kryo.writeObject(output, sendMsgObj)
        val mesg_byte = output.toBytes()

        val request: DatagramPacket = new DatagramPacket(mesg_byte, mesg_byte.length, host, port)
        dataSocket.send(request)

        dataSocket.setSoTimeout(5000)

        val buffer = new Array[Byte](1024)

        val reply = new DatagramPacket(buffer, buffer.length)
        dataSocket.receive(reply)

        val input = new Input(buffer)
        val deser = kryo.readObject(input, classOf[Msg])
        println("Client 01 has Recieved: " + deser.message)
        myRes = deser.message;
      }
      else {
        var sendMsgObj = new End()
        kryo.writeObject(output, sendMsgObj)
        val mesg_byte = output.toBytes()
        val request: DatagramPacket = new DatagramPacket(mesg_byte, mesg_byte.length, host, port)
        dataSocket.send(request)
      }
      //} while (clientInput.toLowerCase() != "end")

    } catch {
      case ex: java.net.SocketTimeoutException => {
        println("Client 01 Exception: " + ex + ". (Note: Please check server, might be Server is not available.)")
      }
      case ex: Throwable =>
        println("Client 01 Exception: " + ex)
        println("Client 01 is now terminating....")

    } finally {
      if (dataSocket != null) {
        dataSocket.close()
      }
    }
  }
}

object myClient01 {
  def main(args: Array[String]) {
    val _myClient1Obj = new _myClient01("Hello From Client 01")
    _myClient1Obj.run()
  }

}
