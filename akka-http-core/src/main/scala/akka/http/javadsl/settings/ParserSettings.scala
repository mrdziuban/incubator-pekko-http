/*
 * Copyright (C) 2017-2020 Lightbend Inc. <https://www.lightbend.com>
 */

package akka.http.javadsl.settings

import java.util.Optional

import akka.actor.{ ActorSystem, ClassicActorSystemProvider }
import akka.http.impl.engine.parsing.BodyPartParser
import akka.http.impl.settings.ParserSettingsImpl
import java.{ util => ju }

import akka.annotation.DoNotInherit
import akka.http.impl.util.JavaMapping.Implicits._

import scala.annotation.varargs
import scala.collection.JavaConverters._
import akka.http.javadsl.model.{ HttpMethod, MediaType, StatusCode, Uri }
import com.github.ghik.silencer.silent
import com.typesafe.config.Config

/**
 * Public API but not intended for subclassing
 */
@DoNotInherit
abstract class ParserSettings private[akka] () extends BodyPartParser.Settings { self: ParserSettingsImpl =>
  def getMaxUriLength: Int
  def getMaxMethodLength: Int
  def getMaxResponseReasonLength: Int
  def getMaxHeaderNameLength: Int
  def getMaxHeaderValueLength: Int
  def getMaxHeaderCount: Int
  def getMaxContentLength: Long
  def getMaxToStrictBytes: Long
  def getMaxChunkExtLength: Int
  def getMaxChunkSize: Int
  def getUriParsingMode: Uri.ParsingMode
  def getCookieParsingMode: ParserSettings.CookieParsingMode
  def getIllegalHeaderWarnings: Boolean
  def getIgnoreIllegalHeaderFor: Set[String]
  def getErrorLoggingVerbosity: ParserSettings.ErrorLoggingVerbosity
  def getIllegalResponseHeaderValueProcessingMode: ParserSettings.IllegalResponseHeaderValueProcessingMode
  def getHeaderValueCacheLimits: ju.Map[String, Int]
  def getIncludeTlsSessionInfoHeader: Boolean
  def headerValueCacheLimits: Map[String, Int]
  def getCustomMethods: java.util.function.Function[String, Optional[HttpMethod]]
  def getCustomStatusCodes: java.util.function.Function[Int, Optional[StatusCode]]
  def getCustomMediaTypes: akka.japi.function.Function2[String, String, Optional[MediaType]]
  def getModeledHeaderParsing: Boolean

  // ---

  def withMaxUriLength(newValue: Int): ParserSettings = self.copy(maxUriLength = newValue)
  def withMaxMethodLength(newValue: Int): ParserSettings = self.copy(maxMethodLength = newValue)
  def withMaxResponseReasonLength(newValue: Int): ParserSettings = self.copy(maxResponseReasonLength = newValue)
  def withMaxHeaderNameLength(newValue: Int): ParserSettings = self.copy(maxHeaderNameLength = newValue)
  def withMaxHeaderValueLength(newValue: Int): ParserSettings = self.copy(maxHeaderValueLength = newValue)
  def withMaxHeaderCount(newValue: Int): ParserSettings = self.copy(maxHeaderCount = newValue)
  def withMaxContentLength(newValue: Long): ParserSettings = self.copy(maxContentLengthSetting = Some(newValue))
  def withMaxToStrictBytes(newValue: Long): ParserSettings = self.copy(maxToStrictBytes = newValue)
  def withMaxChunkExtLength(newValue: Int): ParserSettings = self.copy(maxChunkExtLength = newValue)
  def withMaxChunkSize(newValue: Int): ParserSettings = self.copy(maxChunkSize = newValue)
  def withUriParsingMode(newValue: Uri.ParsingMode): ParserSettings = self.copy(uriParsingMode = newValue.asScala)
  def withCookieParsingMode(newValue: ParserSettings.CookieParsingMode): ParserSettings = self.copy(cookieParsingMode = newValue.asScala)
  def withIllegalHeaderWarnings(newValue: Boolean): ParserSettings = self.copy(illegalHeaderWarnings = newValue)
  def withErrorLoggingVerbosity(newValue: ParserSettings.ErrorLoggingVerbosity): ParserSettings = self.copy(errorLoggingVerbosity = newValue.asScala)
  def withHeaderValueCacheLimits(newValue: ju.Map[String, Int]): ParserSettings = self.copy(headerValueCacheLimits = newValue.asScala.toMap)
  def withIncludeTlsSessionInfoHeader(newValue: Boolean): ParserSettings = self.copy(includeTlsSessionInfoHeader = newValue)
  def withModeledHeaderParsing(newValue: Boolean): ParserSettings = self.copy(modeledHeaderParsing = newValue)
  def withIgnoreIllegalHeaderFor(newValue: List[String]): ParserSettings = self.copy(ignoreIllegalHeaderFor = newValue.map(_.toLowerCase).toSet)

  // special ---

  @varargs
  def withCustomMethods(methods: HttpMethod*): ParserSettings = {
    val map = methods.map(m => m.name -> m.asScala).toMap
    self.copy(customMethods = map.get)
  }
  @varargs
  def withCustomStatusCodes(codes: StatusCode*): ParserSettings = {
    val map = codes.map(c => c.intValue -> c.asScala).toMap
    self.copy(customStatusCodes = map.get)
  }
  @varargs
  def withCustomMediaTypes(mediaTypes: MediaType*): ParserSettings = {
    val map = mediaTypes.map(c => (c.mainType, c.subType) -> c.asScala).toMap
    self.copy(customMediaTypes = (main, sub) => map.get(main -> sub))
  }

}

object ParserSettings extends SettingsCompanion[ParserSettings] {
  trait CookieParsingMode
  trait ErrorLoggingVerbosity
  trait IllegalResponseHeaderValueProcessingMode

  /**
   * @deprecated Use forServer or forClient instead.
   */
  @Deprecated
  @deprecated("Use forServer or forClient instead", since = "10.2.0")
  override def create(config: Config): ParserSettings = ParserSettingsImpl(config)
  /**
   * @deprecated Use forServer or forClient instead.
   */
  @Deprecated
  @deprecated("Use forServer or forClient instead", since = "10.2.0")
  override def create(configOverrides: String): ParserSettings = ParserSettingsImpl(configOverrides)
  /**
   * @deprecated Use forServer or forClient instead.
   */
  @Deprecated
  @deprecated("Use forServer or forClient instead", since = "10.2.0")
  @silent("create overrides concrete, non-deprecated symbol")
  override def create(system: ActorSystem): ParserSettings = create(system.settings.config)

  def forServer(system: ClassicActorSystemProvider): ParserSettings = akka.http.scaladsl.settings.ParserSettings.forServer(system)
  def forClient(system: ClassicActorSystemProvider): ParserSettings = akka.http.scaladsl.settings.ParserSettings.forClient(system)
}
