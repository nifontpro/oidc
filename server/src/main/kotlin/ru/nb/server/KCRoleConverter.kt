package ru.nb.server

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

// класс конвертер из данных JWT в роли spring security
class KCRoleConverter : Converter<Jwt, Collection<GrantedAuthority>> {

	override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
		// получаем доступ к разделу JSON
		val realmAccess = jwt.claims["realm_access"] as? Map<*, *> ?: return emptyList()
		val roles = realmAccess["roles"] as? List<*> ?: return emptyList()

		val returnValue: MutableCollection<GrantedAuthority> = mutableListOf()
		for (role in roles) {// проходим по всем значениям из JSON
			val roleName = role as? String ?: continue
			// создаем объект SimpleGrantedAuthority - это дефолтная реализация GrantedAuthority
//			returnValue.add(SimpleGrantedAuthority("ROLE_$roleName")) // префикс ROLE обязателен
			returnValue.add(SimpleGrantedAuthority(roleName)) // префикс ROLE обязателен
		}
		return returnValue
	}

}