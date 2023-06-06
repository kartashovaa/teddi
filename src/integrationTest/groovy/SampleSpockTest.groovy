import spock.lang.Specification

class SampleSpockTest extends Specification {

    def "run sample test"() {
        when:
        build()
        true

        then:
        false
    }
}
