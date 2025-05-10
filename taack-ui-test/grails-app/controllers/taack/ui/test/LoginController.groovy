/* Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package taack.ui.test

import crew.User
import crew.config.SupportedLanguage
import grails.compiler.GrailsCompileStatic
import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import grails.plugin.springsecurity.LoginController
import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.security.access.annotation.Secured
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.UiFormSpecifier
import taack.ui.dsl.UiMenuSpecifier

@GrailsCompileStatic
@Secured('permitAll')
class LoginController extends LoginController implements GrailsConfigurationAware {

	TaackUiService taackUiService

	private static UiMenuSpecifier buildMenu(String q = null) {
		UiMenuSpecifier m = new UiMenuSpecifier()
		m.ui {
			menuOptions(SupportedLanguage.fromContext())
		}
		m
	}

	def auth() {
		ConfigObject conf = getConf()

		println "COUCOU"

		if ((springSecurityService as SpringSecurityService).isLoggedIn()) {
			println "COUCOU1"
			redirect uri: conf.successHandler["defaultTargetUrl"]
			return
		}
		println "COUCOU2"

		String postUrl = request.contextPath + conf.apf["filterProcessesUrl"]
		println "COUCOU3 $postUrl"

		User user = new User()

		UiFormSpecifier f = new UiFormSpecifier().ui(user) {
			println "COUCOU4 $user"
			section 'Credentials', {
				field user.username_
				field user.password_
			}
			formAction('Login', postUrl)
		}

		println "COUCOU5"
		taackUiService.show(new UiBlockSpecifier().ui {
			form f
		}, buildMenu())
	}

	@Override
	void setConfiguration(Config co) {

	}
}
