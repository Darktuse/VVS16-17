package es.udc.subasta.web.util;

import org.apache.tapestry5.services.Cookies;

public class CookiesManager {

	private static final String LOGIN_NAME_COOKIE = "loginName";
	private static final String ENCRYPTED_PASSWORD_COOKIE = "encryptedPassword";
    private static final int REMEMBER_MY_PASSWORD_AGE =
        30 * 24 * 3600; // 30 days in seconds

	public static void leaveCookies(Cookies cookies, String loginName,
			String encryptedPassword) {
		
		cookies.getBuilder(LOGIN_NAME_COOKIE, loginName).
			setMaxAge(REMEMBER_MY_PASSWORD_AGE).write();
		cookies.getBuilder(ENCRYPTED_PASSWORD_COOKIE, encryptedPassword).
			setMaxAge(REMEMBER_MY_PASSWORD_AGE).write();

	}

	public static void removeCookies(Cookies cookies) {
		cookies.removeCookieValue(LOGIN_NAME_COOKIE);
		cookies.removeCookieValue(ENCRYPTED_PASSWORD_COOKIE);
	}

	public static String getLoginName(Cookies cookies) {
		return cookies.readCookieValue(LOGIN_NAME_COOKIE);
	}

	public static String getEncryptedPassword(Cookies cookies) {
		return cookies.readCookieValue(ENCRYPTED_PASSWORD_COOKIE);
	}

}
