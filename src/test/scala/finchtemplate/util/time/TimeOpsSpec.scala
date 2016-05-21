package finchtemplate.util.time

import finchtemplate.spec.SpecHelper
import org.joda.time.DateTime
import org.specs2.mutable.Specification

final class TimeOpsSpec extends Specification with SpecHelper {
  private val epochString = "1970-01-01T00:00:00.000Z"
  private val epochDateTime = DateTime.parse(epochString)
  private val christmasString = "2015-12-25T00:00:00.000Z"
  private val christmasDateTime = DateTime.parse(christmasString)

  "Conversion from millis" >> {
    "the epoch is the epoch" >> {
      TimeOps.toIso8601(0L) must beEqualTo(epochString)
    }

    "christmas 2015" >> {
      TimeOps.toIso8601(christmasDateTime.getMillis) must beEqualTo(christmasString)
    }
  }

  "Conversion from datetime" >> {
    "the epoch is the epoch" >> {
      TimeOps.toIso8601(epochDateTime) must beEqualTo(epochString)
    }

    "christmas 2015" >> {
      TimeOps.toIso8601(christmasDateTime) must beEqualTo(christmasString)
    }
  }

  "Round trip" >> {
    "conversions work" >> {
      TimeOps.parseAsTime(TimeOps.toIso8601(epochDateTime)) must beEqualTo(Some(epochDateTime))
      TimeOps.parseAsTime(TimeOps.toIso8601(christmasDateTime)) must beEqualTo(Some(DateTime.parse(christmasString)))
    }
  }
}
