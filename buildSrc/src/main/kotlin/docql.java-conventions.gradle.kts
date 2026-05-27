// Convention plugin applied to every plain Java leaf module
plugins {
    id("docql.common-conventions")
    id("docql.maintenance-conventions")
    id("docql.testing-conventions")
    id("docql.packaging-conventions")
}

dependencies {
    compileOnly("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
}
