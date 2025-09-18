package com.exam.examapp;

import lombok.Getter;

@Getter
public enum AppMessage {
    TERM_RETRIEVE_SUCCESS("Termin uğurla əldə edildi."),
    TERM_SAVE_SUCCESS("Termin uğurla yaradıldı."),
    TERM_UPDATE_SUCCESS("Termin uğurla yeniləndi."),
    TERM_DELETE_SUCCESS("Termin uğurla silindi."),
    TERM_NOT_FOUND("Termin tapılmadı."),

    RESOURCE_NOT_FOUND("Resurs tapılmadı"),

    USER_NOT_LOGGED_IN("İstifadəçi sistemə daxil olmayıb"),
    INVALID_CREDENTIALS("Yanlış istifadəçi adı və ya şifrə."),

    VALIDATION_FAILED("Doğrulama uğursuz oldu"),
    MISSING_PARAMETER("Tələb olunan parametr yoxdur"),

    DIRECTORY_CREATE_FAILED("Qovluq yaradıla bilmədi"),
    FILE_SAVE_FAILED("Fayl saxlanıla bilmədi"),
    FILE_DELETE_FAILED("Fayl silinə bilmədi"),

    TERMS_MUST_BE_ACCEPTED("Şərtlər qəbul edilməlidir."),
    ADMIN_CANNOT_BE_REGISTERED("Admin qeydiyyatdan keçirilə bilməz."),
    USER_EXISTS_WITH_EMAIL("Bu email ilə istifadəçi artıq mövcuddur: %s"),
    USER_EXISTS_WITH_PHONE("Bu telefon nömrəsi ilə istifadəçi artıq mövcuddur: %s"),
    USER_REGISTERED_SUCCESS("%s uğurla qeydiyyatdan keçdi."),
    USER_NOT_ACTIVE_OR_DELETED("İstifadəçi aktiv deyil və ya silinib."),
    USER_LOGGED_OUT_SUCCESS("İstifadəçi uğurla çıxış etdi."),
    PASSWORD_RESET_SUCCESS("Şifrə uğurla sıfırlandı. Zəhmət olmasa yeni şifrə ilə daxil olun."),
    INVALID_CODE("Yanlış kod."),
    INVALID_REFRESH_TOKEN("Yanlış refresh token."),
    DONT_COPY_PASTE_LINK("Zəhmət olmasa linki kopyalayıb yapışdırmayın."),

    TERMS_NOT_ACCEPTED("Şərtlər qəbul edilməyib."),
    USER_LOGGED_IN_SUCCESS("İstifadəçi uğurla daxil oldu."),
    TOKEN_REFRESHED_SUCCESS("Token uğurla yeniləndi."),

    EMAIL_NOT_FOUND("Email tapılmadı."),
    EMAIL_SENT("Təsdiq kodu: %s"),
    CODE_VERIFIED_SUCCESS("Kod uğurla təsdiqləndi."),

    CACHE_DELETED("Keş uğurla silindi."),

    JWT_ACCESS_TOKEN_GENERATED("İstifadəçi adı üçün access token yaradılır: %s"),
    JWT_REFRESH_TOKEN_GENERATED("İstifadəçi adı üçün refresh token yaradılır: %s"),
    JWT_TOKEN_INVALID("Token etibarsızdır və ya vaxtı bitib: %s"),

    SEND_EMAIL("Email göndərilir: %s, Mövzu: %s, Məzmun: %s"),
    EMAIL_SENT_SUCCESS("Email uğurla göndərildi.");

    private final String message;

    AppMessage(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }

}
