package io.jiren.util

/** @author Sino */
object ResponseCodes {
  val MayProceed = 0
  val NewLogin = 2
  val InvalidCredentials = 3
  val AccountDisabled = 4
  val AlreadyLoggedIn = 5
  val ClientUpdated = 6
  val ServerFull = 7
  val OfflineLoginServer = 8
  val LoginLimitExceeded = 9
  val BadSessionId = 10
  val ServerBeingUpdated = 14
  val ReconnectToGame = 15
  val TooManyLoginAttempts = 16
  val RunningClosedBeta = 19
  val ProfileTransfer = 20
  val MalformedLoginPacket = 22
  val RetryLogin = 23
  val ErrorLoadingProfile = 24
  val ComputerAddressBlocked = 26
  val ServiceUnavailable = 27
  val ManualLoginRejection = 29
  val AccountTempInaccessible = 37
  val RequireVoteToPlay = 38
  val EnterSixDigitTotpPinCode = 56
  val InvalidTotpPinCode = 57
}
