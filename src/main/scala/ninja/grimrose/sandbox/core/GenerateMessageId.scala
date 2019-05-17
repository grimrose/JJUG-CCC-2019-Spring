package ninja.grimrose.sandbox.core

import java.util.Random

import de.huxhorn.sulky.ulid.ULID
import wvlet.airframe.bind
import wvlet.airframe.codec.MessageCodec
import wvlet.log.LogSupport

trait GenerateMessageId extends LogSupport {

  private val random: Random = bind[Random]

  private val codec: MessageCodec[Map[String, Int]] = MessageCodec.of[Map[String, Int]]

  def handle(): Either[Int, MessageId] = {
    for {
      _ <- process(100)
    } yield MessageId(new ULID(random).nextULID())
  }

  private def process(sample: Int) = {
    val n = random.nextInt(sample)
    info(codec.toJson(Map("generate.number" -> n)))

    n match {
      case a if a % 10 == 0 => Left(a)
      case b => Right(b)
    }
  }

}
