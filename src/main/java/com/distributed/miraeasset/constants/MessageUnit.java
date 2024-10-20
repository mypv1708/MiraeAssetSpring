package com.distributed.miraeasset.constants;

public class MessageUnit {
    public static final String CODE_SUCCESS = "00";
    public static final String MESSAGE_VI_SUCCESS = "Thành công";
    public static final String MESSAGE_EN_SUCCESS = "Success";

    public static final String FORMAT_WRONG = "01";
    public static final String MESSAGE_VI_FORMAT_WRONG = "Sai định dạng";
    public static final String MESSAGE_EN_FORMAT_WRONG = "Wrong format";

    public static final String REQUIRED_FIELDS = "02";
    public static final String MESSAGE_VI_REQUIRED_FIELDS = "Trường bắt buộc";
    public static final String MESSAGE_EN_REQUIRED_FIELDS = "Required fields";

    public static final String NO_MATCHING_FIELDS = "03";
    public static final String MESSAGE_VI_NO_MATCHING_FIELDS = " Mật khẩu không trùng khớp";
    public static final String MESSAGE_EN_NO_MATCHING_FIELDS = "Password do not match";

    public static final String EMAIL_EXITS = "04";
    public static final String MESSAGE_VI_EMAIL_EXITS = "Địa chỉ email đã tồn tại. Vui lòng thử lại email mới";
    public static final String MESSAGE_EN_EMAIL_EXITS = "Email address already exists. Please try a new email";

    public static final String USER_EXITS = "05";
    public static final String MESSAGE_VI_USER_EXITS = "Tên tài khoản đã tồn tại. Vui lòng chọn tên khác";
    public static final String MESSAGE_EN_USER_EXITS = "Account name already exists. Please choose another name";

    public static final String USER_EXITS_WRONG_PASS = "06";
    public static final String MESSAGE_VI_USER_EXITS_WRONG_PASS = "Tài khoản hoặc khẩu không chính xác, xin vui lòng nhập lại";
    public static final String MESSAGE_EN_USER_EXITS_WRONG_PASS = "Your account or password is not correct, please enter again";

    public static final String USER_FORGOT = "07";
    public static final String MESSAGE_VI_USER_FORGOT = "Địa chỉ email không tồn tại trên hệ thống";
    public static final String MESSAGE_EN_USER_FORGOT = "Email address don't exists";

    public static final String FORMAT_PASSWORD_WRONG = "08";
    public static final String MESSAGE_VI_FORMAT_PASSWORD_WRONG = "Độ dài mật khẩu phải từ 8 ký tự trở lên";
    public static final String MESSAGE_EN_FORMAT_PASSWORD_WRONG = "Password length must be 8 characters or more";

    public static final String NOT_FOUND_BRANCH_ID = "09";
    public static final String MESSAGE_VI_NOT_FOUND_BRANCH_ID = "Chi nhánh giao dịch không tồn tại";
    public static final String MESSAGE_EN_NOT_FOUND_BRANCH_ID = "Transaction branch is not exists";

    public static final String NOT_FOUND_ADDRESS_ID = "10";
    public static final String MESSAGE_VI_NOT_FOUND_ADDRESS_ID = "Địa chỉ không tồn tại...";
    public static final String MESSAGE_EN_NOT_FOUND_ADDRESS_ID = "Address is not exists...";

    public static final String INVALID_BRANCH = "12";
    public static final String BRANCH_NOT_EXITS = "Người dùng chưa cập nhập branch trong thông tin cá nhân";

    public static final String INTERNAL_ERROR_SERVER = "500";
    public static final String MESSAGE_VI_INTERNAL_ERROR_SERVER = "Hệ thống đang bị gián đoạn, Quý khách vui lòng thử lại sau";
    public static final String MESSAGE_EN_INTERNAL_ERROR_SERVER = "System has been interrupted,Please try again later";

    public static final String TOKEN_EXPIRED = "401";
    public static final String MESSAGE_VI_TOKEN_EXPIRED = "Thời gian đăng nhập hết hạn. Vui lòng đăng nhập lại";
    public static final String MESSAGE_EN_TOKEN_EXPIRED = "Login time expired. Please login again";
}
