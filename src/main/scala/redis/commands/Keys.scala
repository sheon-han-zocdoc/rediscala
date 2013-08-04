package redis.commands

import akka.util.{ByteString, Timeout}
import redis._
import scala.concurrent.{ExecutionContext, Future}
import redis.protocol._
import redis.protocol.Integer
import redis.protocol.Status
import redis.protocol.Bulk
import scala.util.Try
import scala.concurrent.duration._

trait Keys extends Request {

  def del(keys: String*)(implicit ec: ExecutionContext): Future[Long] =
    send("DEL", keys.map(ByteString.apply)).mapTo[Integer].map(_.toLong)

  def dump(key: String)(implicit ec: ExecutionContext): Future[Option[ByteString]] =
    send("DUMP", Seq(ByteString(key))).mapTo[Bulk].map(_.response)

  def exists(key: String)(implicit ec: ExecutionContext): Future[Boolean] =
    send("EXISTS", Seq(ByteString(key))).mapTo[Integer].map(_.toBoolean)

  def expire(key: String, seconds: Long)(implicit ec: ExecutionContext): Future[Boolean] =
    send("EXPIRE", Seq(ByteString(key), ByteString(seconds.toString))).mapTo[Integer].map(_.toBoolean)

  def expireat(key: String, seconds: Long)(implicit ec: ExecutionContext): Future[Boolean] =
    send("EXPIREAT", Seq(ByteString(key), ByteString(seconds.toString))).mapTo[Integer].map(_.toBoolean)

  def keys(pattern: String)(implicit convert: MultiBulkConverter[Seq[String]], ec: ExecutionContext): Future[Try[Seq[String]]] =
    send("KEYS", Seq(ByteString(pattern))).mapTo[MultiBulk].map(_.asTry[Seq[String]])

  def migrate(host: String, port: Int, key: String, destinationDB: Int, timeout: FiniteDuration, copy: Boolean = false, replace: Boolean = false)(implicit ec: ExecutionContext): Future[Boolean] = {
    var args = Seq[ByteString]()
    if (replace)
      args = ByteString("REPLACE") +: args
    if (copy)
      args = ByteString("COPY") +: args
    send("MIGRATE", Seq(ByteString(host), ByteString(port.toString), ByteString(key), ByteString(destinationDB.toString), ByteString(timeout.toMillis.toString)) ++ args).mapTo[Status].map(_.toBoolean)
  }

  def move(key: String, db: Int)(implicit ec: ExecutionContext): Future[Boolean] =
    send("MOVE", Seq(ByteString(key), ByteString(db.toString))).mapTo[Integer].map(_.toBoolean)

  def `object`() = ??? // TODO

  def persist(key: String)(implicit ec: ExecutionContext): Future[Boolean] =
    send("PERSIST", Seq(ByteString(key))).mapTo[Integer].map(_.toBoolean)

  def pexpire(key: String, milliseconds: Long)(implicit ec: ExecutionContext): Future[Boolean] =
    send("PEXPIRE", Seq(ByteString(key), ByteString(milliseconds.toString))).mapTo[Integer].map(_.toBoolean)

  def pexpireat(key: String, millisecondsTimestamp: Long)(implicit ec: ExecutionContext): Future[Boolean] =
    send("PEXPIREAT", Seq(ByteString(key), ByteString(millisecondsTimestamp.toString))).mapTo[Integer].map(_.toBoolean)

  def pttl(key: String)(implicit ec: ExecutionContext): Future[Long] =
    send("PTTL", Seq(ByteString(key))).mapTo[Integer].map(_.toLong)

  def randomkey()(implicit ec: ExecutionContext): Future[Option[ByteString]] =
    send("RANDOMKEY").mapTo[Bulk].map(_.response)

  def rename(key: String, newkey: String)(implicit ec: ExecutionContext): Future[Boolean] =
    send("RENAME", Seq(ByteString(key), ByteString(newkey))).mapTo[Status].map(_.toBoolean)

  def renamenx(key: String, newkey: String)(implicit ec: ExecutionContext): Future[Boolean] =
    send("RENAMENX", Seq(ByteString(key), ByteString(newkey))).mapTo[Integer].map(_.toBoolean)

  def restore[A](key: String, ttl: Long = 0, serializedValue: A)(implicit convert: RedisValueConverter[A], ec: ExecutionContext): Future[Boolean] =
    send("RESTORE", Seq(ByteString(key), ByteString(ttl.toString), convert.from(serializedValue))).mapTo[Status].map(_.toBoolean)

  def sort = ??? // TODO

  def ttl(key: String)(implicit ec: ExecutionContext): Future[Long] =
    send("TTL", Seq(ByteString(key))).mapTo[Integer].map(_.toLong)

  def `type`(key: String)(implicit ec: ExecutionContext): Future[String] =
    send("TYPE", Seq(ByteString(key))).mapTo[Status].map(_.toString)

}