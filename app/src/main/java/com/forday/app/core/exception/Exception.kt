//package com.forday.app.core.exception
//
//import okio.IOException
//import java.io.FileNotFoundException
//import java.net.SocketTimeoutException
//
//class EmptyLocalDataException(
//    message: String? = "데이터가 존재하지 않습니다.",
//): Exception(message)
//
//class EmptyResponseBodyException(
//    message: String? = "데이터가 존재하지 않습니다.",
//) : Exception(message)
//
///* Network */
///**
// * 인터넷 연결이 좋지 않거나 끊어져 있을 때
// * */
//class NetworkBadConnectionException(
//    message: String? = "네트워크 연결상태가 좋지 않습니다.\n네트워크 상태를 확인해주세요.",
//): IOException(message)
//
///**
// * URL이 잘못 돼 있어서 API request resource를 찾을 수 없거나 (404 Page Not Found : HttpURLConnection.HTTP_NOT_FOUND)
// * 전송하는 파라미터 값이 너무 길 때 (414 Request-URI Too Large : HttpURLConnection.HTTP_REQ_TOO_LONG)
// *
// * ※ 404 인지 414인지는 서버로부터 받아야하기 때문에 나중에 시간이 지나면 서버에서 statusCode를 받아 처리하도록 변경 필요할 수도 있음
// * */
//class NetworkBadUrlException(
//    message: String? = "네트워크 요청 주소가 잘 못 되었습니다.\n관리자에게 문의해주세요.",
//): IOException(message)
//
///**
// * 네트워크 요청시간이 지정한 시간보다 길 때
// * */
//class NetworkSocketTimeoutException(
//    message: String? = "네트워크 연결에 지연이 발생했습니다.\n잠시 후 다시 시도해 주세요.",
//): SocketTimeoutException(message)
//
//class EmptyFileException(
//    message: String? = "빈 파일을 보낼 수 없습니다.\n관리자에게 문의해주세요.",
//): FileNotFoundException(message)
//
///**
// * 500번대 서버 에러
// * */
//class ServerException(
//    errorInfo: String,
//    message: String? = "서버 오류가 발생했습니다.\n관리자에게 문의해주세요."
//): IllegalStateException(message)
//
///**
// * 400번대 클라이언트 에러
// * */
//class ClientException(
//    errorInfo: String,
//    message: String? = "클라이언트 오류가 발생했습니다.\n관리자에게 문의해주세요."
//): IllegalStateException(message)