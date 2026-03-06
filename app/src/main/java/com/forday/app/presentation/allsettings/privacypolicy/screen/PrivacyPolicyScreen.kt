package com.forday.app.presentation.allsettings.privacypolicy.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import com.forday.app.core.designsystem.component.clickable.NoRippleInteractionSource
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dayn.forday.R
import com.forday.app.core.designsystem.theme.ForDayTheme

@Composable
fun PrivacyPolicyScreen(
    onCloseClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        // Header
        PrivacyPolicyHeader(onCloseClick = onCloseClick)

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
                // 개인정보처리방침 제목
                Text(
                    text = "개인정보처리방침",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 서문
                PrivacySection(
                    title = "",
                    content = """데이앤(이하 "회사")은 「개인정보 보호법」 제30조에 따라 정보주체의 개인정보를 보호하고 이와 관련한 고충을 신속하고 원활하게 처리할 수 있도록 다음과 같이 개인정보처리방침을 수립·공개합니다."""
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제1조 (개인정보의 처리 목적)
                PrivacySectionWithNumberedList(
                    title = "제1조 (개인정보의 처리 목적)",
                    intro = """회사는 다음의 목적을 위하여 개인정보를 처리합니다. 처리하고 있는 개인정보는 다음의 목적 이외의 용도로는 이용되지 않으며, 이용 목적이 변경되는 경우에는 「개인정보 보호법」 제18조에 따라 별도의 동의를 받는 등 필요한 조치를 이행할 예정입니다.""",
                    items = listOf(
                        """회원 가입 및 관리
   회원 식별, 서비스 이용 의사 확인, 연령 확인, 부정 이용 방지""",
                        """서비스 제공
   취미 활동 기록 및 관리, 소셜 피드 제공, 친구 기능, AI 활동 추천, 클래스 및 모임 추천""",
                        """서비스 개선 및 개발
   서비스 이용 분석, 신규 서비스 개발, 맞춤형 콘텐츠 제공""",
                        """마케팅 및 광고 활용 (선택 동의 시)
   이벤트 안내, 맞춤형 광고 제공"""
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제2조 (처리하는 개인정보의 항목)
                PrivacySectionWithNumberedList(
                    title = "제2조 (처리하는 개인정보의 항목)",
                    intro = "회사는 다음의 개인정보 항목을 처리하고 있습니다.",
                    items = listOf(
                        """소셜 로그인 (카카오, 애플)
   필수 항목: 소셜 계정 식별자, 닉네임
   선택 항목: 프로필 사진, 이메일 (소셜 플랫폼에서 제공하는 경우)""",
                        """게스트 로그인
   필수 항목: 닉네임, 취미 종류""",
                        """서비스 이용 과정에서 수집되는 정보
   필수 항목: 취미 종류, 취미 시간, 취미 횟수, 취미 빈도, 취미 활동명
   선택 항목: 활동 사진, 활동 메모, 프로필 사진, 취미 대표 사진""",
                        """자동 수집 정보
   서비스 이용 기록, 접속 로그, IP 주소, 쿠키, 기기 정보 (OS 버전, 기기 모델 등)"""
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제3조 (개인정보의 처리 및 보유 기간)
                PrivacySectionWithNumberedList(
                    title = "제3조 (개인정보의 처리 및 보유 기간)",
                    intro = """회사는 법령에 따른 개인정보 보유·이용 기간 또는 정보주체로부터 개인정보를 수집 시에 동의받은 개인정보 보유·이용 기간 내에서 개인정보를 처리·보유합니다.
각각의 개인정보 처리 및 보유 기간은 다음과 같습니다:""",
                    items = listOf(
                        "회원 가입 및 관리: 회원 탈퇴 시까지 (단, 관련 법령에 따라 보존 필요 시 해당 기간까지)",
                        "서비스 제공: 서비스 이용 종료 시까지",
                        """관련 법령에 따라 보존이 필요한 경우:
   • 계약 또는 청약철회 등에 관한 기록: 5년 (전자상거래법)
   • 대금결제 및 재화 등의 공급에 관한 기록: 5년 (전자상거래법)
   • 소비자 불만 또는 분쟁처리 기록: 3년 (전자상거래법)
   • 서비스 방문 기록: 3개월 (통신비밀보호법)"""
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제4조 (개인정보의 제3자 제공)
                PrivacySection(
                    title = "제4조 (개인정보의 제3자 제공)",
                    content = """회사는 정보주체의 개인정보를 제1조(개인정보의 처리 목적)에서 명시한 범위 내에서만 처리하며, 정보주체의 동의, 법률의 특별한 규정 등 「개인정보 보호법」 제17조 및 제18조에 해당하는 경우에만 개인정보를 제3자에게 제공합니다.

현재 회사는 이용자의 개인정보를 제3자에게 제공하지 않습니다. 향후 제공이 필요한 경우 사전에 동의를 받겠습니다."""
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제5조 (개인정보 처리의 위탁)
                PrivacySection(
                    title = "제5조 (개인정보 처리의 위탁)",
                    content = """회사는 개인정보 처리 업무를 위탁함에 있어 개인정보보호법 제29조에 따라 개인정보가 안전하게 관리될 수 있도록 기술적·관리적 보호조치를 시행하고 있습니다.

위탁받는 자: Amazon Web Services (AWS)
위탁 업무 내용: 데이터 보관 및 서버 운영
보유 및 이용기간: 회원 탈퇴 시 또는 위탁 계약 종료 시까지

위탁 업체는 서비스 운영 상황에 따라 변경될 수 있으며, 변경 시 공지합니다.

AWS는 서비스 제공을 위해 필요한 범위 내에서 일부 업무를 재위탁(Sub-processor)할 수 있습니다.

AWS의 재위탁 현황은 AWS에서 공개하는 하위 처리자 목록을 통해 확인할 수 있으며, 회사는 관련 법령에 따라 개인정보 보호 수준이 유지되도록 관리·감독합니다.

위탁받은 자에 대하여는 개인정보에 대한 접근 통제, 접근 권한의 최소화, 개인정보의 암호화, 보안 프로그램 설치 및 운영 등 안전성 확보에 필요한 조치를 취하도록 관리·감독합니다."""
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제6조 (정보주체의 권리·의무 및 행사 방법)
                PrivacySectionWithNumberedList(
                    title = "제6조 (정보주체의 권리·의무 및 행사 방법)",
                    intro = "정보주체는 회사에 대해 언제든지 다음 각 호의 개인정보 보호 관련 권리를 행사할 수 있습니다:",
                    items = listOf(
                        "개인정보 열람 요구",
                        "오류 등이 있을 경우 정정 요구",
                        "삭제 요구",
                        "처리 정지 요구",
                        "권리 행사는 서비스 내 설정 메뉴 또는 이메일(team.forday@gmail.com)을 통해 할 수 있습니다.",
                        "만 14세 미만 아동의 경우 법정대리인이 권리를 행사할 수 있습니다."
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제7조 (개인정보의 파기)
                PrivacySection(
                    title = "제7조 (개인정보의 파기)",
                    content = """회사는 개인정보 보유 기간의 경과, 처리 목적 달성 등 개인정보가 불필요하게 되었을 때에는 지체 없이 해당 개인정보를 파기합니다.

파기 절차 및 방법은 다음과 같습니다:
   • 전자적 파일: 복구 및 재생이 불가능한 방법으로 영구 삭제
   • 종이 문서: 분쇄 또는 소각"""
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제8조 (개인정보의 안전성 확보 조치)
                PrivacySectionWithNumberedList(
                    title = "제8조 (개인정보의 안전성 확보 조치)",
                    intro = "회사는 개인정보의 안전성 확보를 위해 다음과 같은 조치를 취하고 있습니다:",
                    items = listOf(
                        "개인정보 암호화",
                        "해킹 등에 대비한 기술적 대책",
                        "개인정보 취급 직원의 최소화 및 교육",
                        "개인정보 접근 권한 관리"
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제9조 (개인정보 자동 수집 장치의 설치·운영 및 거부)
                PrivacySectionWithNumberedList(
                    title = "제9조 (개인정보 자동 수집 장치의 설치·운영 및 거부)",
                    items = listOf(
                        "회사는 서비스 제공을 위해 쿠키를 사용할 수 있습니다.",
                        "쿠키는 서비스 이용 편의성 향상, 맞춤형 서비스 제공을 위해 사용됩니다.",
                        "이용자는 웹브라우저 설정을 통해 쿠키 저장을 거부할 수 있으나, 이 경우 서비스 이용에 제한이 있을 수 있습니다."
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제10조 (개인정보 보호책임자)
                PrivacySection(
                    title = "제10조 (개인정보 보호책임자)",
                    content = """회사는 개인정보 처리에 관한 업무를 총괄해서 책임지고, 개인정보 처리와 관련한 정보주체의 불만 처리 및 피해구제 등을 위하여 아래와 같이 개인정보 보호책임자를 지정하고 있습니다.

개인정보 보호책임자
이메일: team.forday@gmail.com

정보주체는 서비스를 이용하며 발생한 모든 개인정보 보호 관련 문의, 불만 처리, 피해구제 등에 관한 사항을 개인정보 보호책임자에게 문의하실 수 있습니다."""
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제11조 (권익침해 구제 방법)
                PrivacySectionWithNumberedList(
                    title = "제11조 (권익침해 구제 방법)",
                    intro = "정보주체는 개인정보 침해로 인한 구제를 받기 위하여 개인정보분쟁조정위원회, 한국인터넷진흥원 개인정보침해신고센터 등에 분쟁해결이나 상담 등을 신청할 수 있습니다.",
                    items = listOf(
                        "개인정보분쟁조정위원회: 1833-6972 (www.kopico.go.kr)",
                        "개인정보침해신고센터: 118 (privacy.kisa.or.kr)",
                        "대검찰청 사이버수사과: 1301 (www.spo.go.kr)",
                        "경찰청 사이버안전국: 182 (cyberbureau.police.go.kr)"
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 제12조 (개인정보처리방침의 변경)
                PrivacySectionWithNumberedList(
                    title = "제12조 (개인정보처리방침의 변경)",
                    items = listOf(
                        "이 개인정보처리방침은 [2026-02-07] 부터 적용됩니다.",
                        "본 방침은 법령, 정책 또는 보안기술의 변경에 따라 내용의 추가, 삭제 및 수정이 있을 시에는 변경사항 시행 7일 전부터 공지사항을 통해 고지할 것입니다."
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 문의처
                PrivacySection(
                    title = "문의처",
                    content = """서비스명: 포데이 (FORDAY)
운영: 데이앤 (DayN)
이메일: team.forday@gmail.com"""
                )
            }
        }
    }
}

@Composable
private fun PrivacyPolicyHeader(
    onCloseClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFFF9F9F9))
    ) {
        Text(
            text = "개인정보 처리방침",
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
private fun PrivacySection(
    title: String,
    content: String
) {
    Column {
        // 제목 (있는 경우만)
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 내용
        Text(
            text = content,
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 16.8.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF3A3A3A),
            )
        )
    }
}

@Composable
private fun PrivacySectionWithNumberedList(
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
                    lineHeight = 16.8.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF3A3A3A),
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
                        lineHeight = 16.8.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF3A3A3A),
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
private fun PrivacyPolicyScreenPreview() {
    ForDayTheme {
        PrivacyPolicyScreen(onCloseClick = {})
    }
}