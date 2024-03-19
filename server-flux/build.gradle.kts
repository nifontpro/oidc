plugins {
	id("spring-core")
}

dependencies {
	implementation(project(":base"))

	implementation("org.springframework.boot:spring-boot-starter-webflux")
//	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
}