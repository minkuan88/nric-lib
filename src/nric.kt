/**
 * NricFinValidator.kt
 *
 * Utility to validate NRIC/FIN based on algorithm that can be found online
 *
 * @see [http://www.ngiam.net/NRIC/ppframe.htm]
 * @see [https://sites.google.com/site/tccgroupit/technical-corner/ic-validation]
 * @see [https://github.com/mjallday/nric-lib]
 */
object NricFinValidator {

  private val multiples = arrayOf(2, 7, 6, 5, 4, 3, 2)
  private val uinChecksums = arrayOf('J', 'Z', 'I', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A')
  private val finChecksums = arrayOf('X', 'W', 'U', 'T', 'R', 'Q', 'P', 'N', 'M', 'L', 'K')

  fun validate(input: String) =
    isValidFormat(input)
        && hasValidChecksum(input)

  private fun isValidFormat(input: String): Boolean =
    Regex("^(?i)[STFG][0-9]{7}(?:(?![OSVYZ])[A-Z])$")
      .let(input::matches)

  private fun hasValidChecksum(input: String): Boolean =
    getModulo(input)
      .let { mod ->
        if (isUin(input)) {
          uinChecksums[mod]
        } else {
          finChecksums[mod]
        }.equals(input.last(), true)
      }

  private fun getModulo(input: String): Int =
    getNumeric(input)
      .reduceIndexed { index, acc, d ->
        if (index == 1) {
          // reduce starts with index 1 and acc = value[0]
          acc * multiples[0] + d * multiples[index]
        } else {
          acc + d * multiples[index]
        }
      }
      .let { acc ->
        if (
          input.startsWith("T", true)
          || input.startsWith("G", true)
        ) {
          acc + 4
        } else {
          acc + 0
        }
      }.mod(11)

  private fun getNumeric(input: String): List<Int> =
    input
      .drop(1)
      .dropLast(1)
      .toCharArray()
      .map(Character::getNumericValue)

  private fun isUin(input: String): Boolean =
    input.startsWith("S", true)
        || input.startsWith("T", true)

  private fun isFin(input: String): Boolean =
    input.startsWith("F", true)
        || input.startsWith("G", true)
}
