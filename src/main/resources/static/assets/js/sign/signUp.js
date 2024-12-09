document.getElementById("signupForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  if (!isEmailVerified) {
    alert("이메일 인증이 필요합니다.");
    return;
  }

  const messageEl = document.getElementById("idCheckMessage");
  if (!messageEl.textContent || messageEl.style.color === "#FFCC00") {
    alert("아이디 중복 확인이 필요합니다.");
    return;
  }

  const pwd = document.getElementById("signupPwd").value;
  const pwdConfirm = document.getElementById("signupPwdConfirm").value;

  if (pwd !== pwdConfirm) {
    alert("비밀번호가 일치하지 않습니다.");
    return;
  }

  try {
    const response = await fetch("/sign/signup", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        memberId: document.getElementById("signupId").value,
        pwd: document.getElementById("signupPwd").value,
        name: document.getElementById("signupName").value,
        email: document.getElementById("signupEmail").value,
        phone: document.getElementById("signupPhone").value,
        social: "N",
      }),
    });
    if (response.ok) {
      alert("회원가입이 완료되었습니다.");
      window.location.href = "/";
    } else {
      throw new Error("회원가입 실패");
    }
  } catch (error) {
    document.getElementById("signupError").style.display = "block";
    document.getElementById("signupError").textContent = error.message;
  }
});

// 아이디 유효성 검사 함수
function validateId(input) {
  const messageEl = document.getElementById("idCheckMessage");
  const idRegex = /^(?=.*[a-z])(?=.*\d)[a-z0-9]{6,}$/;

  if (!idRegex.test(input.value)) {
    messageEl.style.color = "#FFCC00";
    messageEl.textContent =
      "아이디는 영어 소문자와 숫자를 포함한 6자리 이상이어야 합니다.";
    return false;
  }
  messageEl.textContent = "";
  return true;
}

// 아이디 중복 체크 함수
async function checkMemberId() {
  const memberId = document.getElementById("signupId").value;
  const messageEl = document.getElementById("idCheckMessage");

  if (!validateId({ value: memberId })) {
    return;
  }

  try {
    const response = await fetch(`/sign/check/${memberId}`);
    const data = await response.json();

    messageEl.style.color = data.duplicate ? "#FFCC00" : "white";
    messageEl.textContent = data.message;
  } catch (error) {
    messageEl.style.color = "#FFCC00";
    messageEl.textContent = error.message;
  }
}

let timerInterval;
let isEmailVerified = false;

function validateEmail(input) {
  const messageEl = document.getElementById("emailValidMessage");
  const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;

  if (!emailRegex.test(input.value)) {
    messageEl.style.display = "block";
    return false;
  } else {
    messageEl.style.display = "none";
    return true;
  }
}

async function sendVerificationEmail() {
  const email = document.getElementById("signupEmail").value;
  const messageEl = document.getElementById("emailValidMessage");

  if (!validateEmail({ value: email })) {
    alert("유효한 이메일 주소를 입력해주세요.");
    return;
  }

  try {
    const response = await fetch("/sign/send-verification-email", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email }),
    });

    const data = await response.json();
    if (response.ok) {
      document.getElementById("verificationSection").style.display = "block";
      startTimer(300);
      alert(data.message);
    } else {
      throw new Error(data.error);
    }
  } catch (error) {
    alert(error.message);
  }
}

function startTimer(duration) {
  clearInterval(timerInterval);
  const timerDisplay = document.getElementById("timer");
  let timer = duration;

  timerInterval = setInterval(() => {
    const minutes = Math.floor(timer / 60);
    const seconds = timer % 60;

    timerDisplay.textContent = `${minutes}:${seconds
      .toString()
      .padStart(2, "0")}`;

    if (--timer < 0) {
      clearInterval(timerInterval);
      timerDisplay.textContent = "시간 만료";
    }
  }, 1000);
}

async function verifyEmail() {
  const code = document.getElementById("verificationCode").value;
  const email = document.getElementById("signupEmail").value;
  const messageEl = document.getElementById("emailVerificationMessage");

  try {
    const response = await fetch("/sign/verify-email", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email,
        code,
      }),
    });

    if (response.ok) {
      messageEl.style.color = "green";
      messageEl.textContent = "이메일 인증이 완료되었습니다.";
      isEmailVerified = true;
      clearInterval(timerInterval);
    } else {
      messageEl.style.color = "#FFCC00";
      messageEl.textContent = "인증번호가 일치하지 않습니다.";
      isEmailVerified = false;
    }
  } catch (error) {
    messageEl.style.color = "#FFCC00";
    messageEl.textContent = "인증 확인 중 오류가 발생했습니다.";
    isEmailVerified = false;
  }
}

document
  .getElementById("signupPwdConfirm")
  .addEventListener("input", function () {
    const pwd = document.getElementById("signupPwd").value;
    const pwdConfirm = this.value;
    const messageEl = document.getElementById("pwdMatchMessage");

    if (pwd !== pwdConfirm) {
      messageEl.style.display = "block";
    } else {
      messageEl.style.display = "none";
    }
  });

function validatePassword(input) {
  const messageEl = document.getElementById("pwdValidMessage");
  if (
    !input.value.match(
      /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':\"\\|,.<>\/?]).{10,20}$/
    )
  ) {
    messageEl.style.display = "block";
  } else {
    messageEl.style.display = "none";
  }
}

function validateName(input) {
  const messageEl = document.getElementById("nameValidMessage");
  const nameRegex = /^[가-힣]{2,20}$/;

  if (!nameRegex.test(input.value)) {
    messageEl.style.display = "block";
    messageEl.textContent = "이름을 정확히 입력해주세요.";
    return false;
  } else {
    messageEl.style.display = "none";
    return true;
  }
}

function validatePhone(input) {
  const messageEl = document.getElementById("phoneValidMessage");
  if (!input.value.match(/^\d{11}$/)) {
    messageEl.style.display = "block";
  } else {
    messageEl.style.display = "none";
  }
}
