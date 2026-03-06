package com.forday.app.presentation.allsettings.termsofservice.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dayn.forday.R
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import com.forday.app.core.designsystem.theme.ForDayTheme

@Composable
fun TermsOfServiceScreen(
    onCloseClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        // Header
        TermsOfServiceHeader(onCloseClick = onCloseClick)

        // Content (Scrollable)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
            ) {
                // 포데이(FORDAY) 이용약관 제목
                Text(
                    text = "포데이(FORDAY) 이용약관",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제1조 (목적)
                TermsSection(
                    title = "제1조 (목적)",
                    content = """본 약관은 데이앤(이하 "회사")이 제공하는 포데이(FORDAY) 서비스(이하 "서비스")의 이용과 관련하여 회사와 이용자 간의 권리, 의무 및 책임사항, 기타 필요한 사항을 규정함을 목적으로 합니다."""
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제2조 (정의)
                TermsSectionWithNumberedList(
                    title = "제2조 (정의)",
                    intro = "본 약관에서 사용하는 용어의 정의는 다음과 같습니다.",
                    items = listOf(
                        """"서비스"란 회사가 제공하는 취미 활동 기록, 소셜 피드, 친구 기능, AI 활동 추천, 클래스 및 모임 추천 등의 모든 서비스를 말합니다.""",
                        """"이용자"란 본 약관에 동의하고 서비스를 이용하는 자를 말합니다.""",
                        """"회원"란 소셜 로그인 또는 게스트 로그인을 통해 서비스에 가입하여 지속적으로 서비스를 이용할 수 있는 자를 말합니다.""",
                        """"게스트"란 회원가입 없이 닉네임과 취미 정보만으로 제한적으로 서비스를 이용하는 자를 말합니다.""",
                        """"콘텐츠"란 이용자가 서비스에 게시한 취미 활동 기록, 사진, 메모 등의 정보를 말합니다."""
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제3조 (약관의 효력 및 변경)
                TermsSectionWithNumberedList(
                    title = "제3조 (약관의 효력 및 변경)",
                    items = listOf(
                        "본 약관은 서비스 화면에 게시하거나 기타의 방법으로 이용자에게 공지함으로써 효력이 발생합니다.",
                        "회사는 필요한 경우 관련 법령을 위배하지 않는 범위 내에서 본 약관을 변경할 수 있습니다.",
                        "약관이 변경되는 경우 회사는 변경사항을 시행일자 7일 전부터 공지하며, 중대한 변경사항의 경우 30일 전에 공지합니다.",
                        "이용자가 변경된 약관에 동의하지 않는 경우 서비스 이용을 중단하고 탈퇴할 수 있습니다."
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제4조 (이용 계약의 성립)
                TermsSectionWithNumberedList(
                    title = "제4조 (이용 계약의 성립)",
                    items = listOf(
                        "이용 계약은 이용자가 본 약관에 동의하고 회원가입을 완료함으로써 성립됩니다.",
                        """회원가입은 다음의 방법으로 가능합니다:
   • 소셜 로그인 (카카오, 애플)
   • 게스트 로그인 (닉네임, 취미 정보 입력)""",
                        """회사는 다음 각 호에 해당하는 경우 가입을 거부하거나 사후에 이용 계약을 해지할 수 있습니다:
   • 만 12세 미만인 경우
   • 타인의 정보를 도용한 경우
   • 허위 정보를 기재한 경우
   • 이전에 회원 자격을 상실한 적이 있는 경우 (단, 회사의 재가입 승낙을 받은 경우 제외)"""
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제5조 (개인정보의 보호)
                TermsSectionWithNumberedList(
                    title = "제5조 (개인정보의 보호)",
                    items = listOf(
                        "회사는 관련 법령이 정하는 바에 따라 이용자의 개인정보를 보호하기 위해 노력합니다.",
                        "개인정보의 보호 및 이용에 대해서는 관련 법령 및 회사의 개인정보처리방침이 적용됩니다.",
                        """회사가 수집하는 개인정보는 다음과 같습니다:
   • 필수 정보: 닉네임, 취미 종류, 취미 시간, 취미 횟수, 취미 빈도, 취미 활동명
   • 선택 정보: 활동 사진, 활동 메모, 프로필 사진, 취미 대표 사진
   • 소셜 로그인 시: 해당 플랫폼에서 제공하는 정보 (이메일, 프로필 정보 등)"""
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제6조 (서비스의 제공 및 변경)
                TermsSectionWithNumberedList(
                    title = "제6조 (서비스의 제공 및 변경)",
                    items = listOf(
                        """회사는 다음과 같은 서비스를 제공합니다:
   • 취미 활동 기록 및 관리
   • 소셜 피드 (다른 이용자의 활동 보기)
   • 친구 추가 및 관리
   • AI 기반 활동 추천
   • 클래스 및 모임 추천""",
                        "회사는 서비스의 내용을 변경할 수 있으며, 변경 시 사전에 공지합니다.",
                        "서비스는 연중무휴, 1일 24시간 제공함을 원칙으로 합니다. 다만, 시스템 점검, 서버 증설 등 운영상 필요한 경우 서비스를 일시 중단할 수 있습니다."
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제7조 (유료 서비스)
                TermsSectionWithNumberedList(
                    title = "제7조 (유료 서비스)",
                    items = listOf(
                        "현재 서비스는 무료로 제공됩니다.",
                        "회사는 향후 일부 서비스를 유료로 전환하거나 유료 서비스를 추가할 수 있습니다.",
                        "유료 서비스 도입 시 회사는 최소 30일 전에 이용자에게 공지하며, 이용자는 유료 서비스 이용 여부를 선택할 수 있습니다.",
                        "유료 서비스 이용 시 별도의 결제 약관이 적용됩니다."
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제8조 (이용자의 의무)
                TermsSectionWithNumberedList(
                    title = "제8조 (이용자의 의무)",
                    items = listOf(
                        """이용자는 다음 행위를 해서는 안 됩니다:
   • 타인의 정보 도용
   • 허위 정보 기재
   • 회사 또는 타인의 지적재산권 침해
   • 욕설, 비방, 음란물 등 부적절한 콘텐츠 게시
   • 스팸, 광고성 정보 무단 게시
   • 서비스 운영 방해 행위
   • 기타 관련 법령 위반 행위""",
                        "이용자는 본 약관, 관련 법령, 회사의 정책을 준수해야 합니다.",
                        "이용자가 위 의무를 위반한 경우 회사는 서비스 이용을 제한하거나 이용 계약을 해지할 수 있습니다."
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제9조 (콘텐츠의 저작권)
                TermsSectionWithNumberedList(
                    title = "제9조 (콘텐츠의 저작권)",
                    items = listOf(
                        "이용자가 작성한 콘텐츠의 저작권은 이용자에게 귀속됩니다.",
                        """이용자는 자신이 작성한 콘텐츠에 대해 회사가 다음의 목적으로 사용하는 것에 동의합니다:
   • 서비스 내 게시 및 전송
   • 서비스 홍보 및 마케팅 (사전 동의 시)
   • 서비스 개선 및 AI 학습""",
                        "회사가 작성한 콘텐츠(AI 추천 내용 등)의 저작권은 회사에 귀속됩니다.",
                        "이용자는 타인의 저작권을 침해하는 콘텐츠를 게시해서는 안 됩니다."
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제10조 (게시물의 관리)
                TermsSectionWithNumberedList(
                    title = "제10조 (게시물의 관리)",
                    items = listOf(
                        """이용자의 게시물이 다음에 해당하는 경우 회사는 사전 통지 없이 삭제하거나 이동할 수 있습니다:
   • 타인을 비방하거나 명예를 훼손하는 내용
   • 음란물, 폭력적 내용
   • 범죄 행위와 관련된 내용
   • 타인의 저작권 등 권리를 침해하는 내용
   • 회사 또는 제3자의 권리를 침해하는 내용
   • 기타 관련 법령에 위반되는 내용""",
                        "회사는 게시물로 인한 법적 분쟁 발생 시 해당 게시물을 임시 차단할 수 있습니다."
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제11조 (서비스 이용 제한)
                TermsSectionWithNumberedList(
                    title = "제11조 (서비스 이용 제한)",
                    items = listOf(
                        "회사는 이용자가 본 약관을 위반하거나 서비스의 정상적인 운영을 방해한 경우 서비스 이용을 제한하거나 정지할 수 있습니다.",
                        """이용 제한의 종류는 다음과 같습니다:
   • 경고
   • 일시 정지 (최대 30일)
   • 영구 정지""",
                        "회사는 이용 제한 시 이용자에게 그 사유와 기간을 통지합니다.",
                        "이용자는 이용 제한에 대해 이의가 있는 경우 회사에 소명할 수 있습니다."
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제12조 (서비스 이용의 종료)
                TermsSectionWithNumberedList(
                    title = "제12조 (서비스 이용의 종료)",
                    items = listOf(
                        "이용자는 언제든지 서비스 내 설정을 통해 탈퇴할 수 있습니다.",
                        "탈퇴 시 이용자의 모든 데이터는 삭제되며, 삭제된 데이터는 복구할 수 없습니다. 단, 관련 법령에 따라 보관이 필요한 정보는 일정 기간 보관됩니다.",
                        """회사는 다음의 경우 이용 계약을 해지할 수 있습니다:
   • 이용자가 본 약관을 위반한 경우
   • 6개월 이상 서비스를 이용하지 않은 경우 (휴면 계정 전환)"""
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제13조 (손해배상 및 면책)
                TermsSectionWithNumberedList(
                    title = "제13조 (손해배상 및 면책)",
                    items = listOf(
                        "회사는 무료로 제공되는 서비스 이용과 관련하여 관련 법령에 특별한 규정이 없는 한 책임을 지지 않습니다.",
                        "회사는 천재지변, 전쟁, 해킹, DDOS 공격 등 불가항력으로 인해 서비스를 제공할 수 없는 경우 책임을 지지 않습니다.",
                        "회사는 이용자의 귀책사유로 인한 서비스 이용 장애에 대해 책임을 지지 않습니다.",
                        "회사는 이용자 간 또는 이용자와 제3자 간에 발생한 분쟁에 대해 책임을 지지 않습니다.",
                        "이용자가 본 약관을 위반하여 회사에 손해를 입힌 경우 그에 대한 모든 책임을 부담합니다."
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제14조 (분쟁 해결)
                TermsSectionWithNumberedList(
                    title = "제14조 (분쟁 해결)",
                    items = listOf(
                        "서비스 이용과 관련한 분쟁은 회사와 이용자 간 협의를 통해 해결합니다.",
                        "협의가 이루어지지 않을 경우 「소비자기본법」에 따른 소비자분쟁조정기구의 조정을 거칠 수 있습니다.",
                        "소송이 필요한 경우 대한민국 법을 준거법으로 하며, 회사의 본사 소재지 법원을 관할 법원으로 합니다."
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제15조 (통지)
                TermsSectionWithNumberedList(
                    title = "제15조 (통지)",
                    items = listOf(
                        "회사가 이용자에게 통지하는 경우 본 약관에 별도 규정이 없는 한 서비스 내 공지사항, 이메일, 푸시 알림 등으로 할 수 있습니다.",
                        "회사는 불특정 다수 이용자에 대한 통지의 경우 서비스 내 공지로 개별 통지를 갈음할 수 있습니다."
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 부칙
                TermsSection(
                    title = "부칙",
                    content = "본 약관은 [2026-02-07]부터 시행됩니다."
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 문의처
                Column {
                    Text(
                        text = "문의처",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3A3A3A)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val uriHandler = LocalUriHandler.current
                    val linkUrl = "https://www.notion.so/2fdb17d4cc2d80268d3fd1d8394d9d2f?source=copy_link"

                    val annotatedString = buildAnnotatedString {
                        append("서비스명: 포데이 (FORDAY)\n")
                        append("운영: 데이앤 (DayN)\n")
                        append("이메일: team.forday@gmail.com\n")
                        append("제 1차 이용약관: ")

                        // 링크 부분을 파란색으로, 밑줄 추가
                        pushStringAnnotation(
                            tag = "URL",
                            annotation = linkUrl
                        )
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF007AFF), // 파란색
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                            )
                        ) {
                            append(linkUrl)
                        }
                        pop()
                    }

                    ClickableText(
                        text = annotatedString,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF4A4A4A),
                            lineHeight = 20.sp
                        ),
                        onClick = { offset ->
                            annotatedString.getStringAnnotations(
                                tag = "URL",
                                start = offset,
                                end = offset
                            ).firstOrNull()?.let { annotation ->
                                uriHandler.openUri(annotation.item)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TermsOfServiceHeader(
    onCloseClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFFF9F9F9))
    ) {
        Text(
            text = "서비스 이용약관",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            ),
            modifier = Modifier.align(Alignment.Center)
        )

        IconButton(
            onClick = onCloseClick,
            interactionSource = remember { NoRippleInteractionSource() },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "닫기",
                tint = Color.Black
            )
        }
    }
}

@Composable
private fun TermsSection(
    title: String,
    content: String
) {
    Column {
        // 제목
        Text(
            text = title,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3A3A3A)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 내용
        Text(
            text = content,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF4A4A4A),
                lineHeight = 20.sp
            )
        )
    }
}

@Composable
private fun TermsSectionWithNumberedList(
    title: String,
    intro: String? = null,
    items: List<String>
) {
    Column {
        // 제목
        Text(
            text = title,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3A3A3A)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 서두 (있는 경우)
        intro?.let {
            Text(
                text = it,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4A4A4A),
                    lineHeight = 20.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 번호가 있는 항목들
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 번호
                Text(
                    text = "${index + 1}. ",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF4A4A4A),
                        lineHeight = 20.sp
                    )
                )

                // 내용 (개행 시 들여쓰기 적용)
                Text(
                    text = item,
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 16.8.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF3A3A3A),
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            if (index < items.size - 1) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TermsOfServiceScreenPreview() {
    ForDayTheme {
        TermsOfServiceScreen(
            onCloseClick = {}
        )
    }
}