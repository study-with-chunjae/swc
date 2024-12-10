document.getElementById("signinForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    const response = await fetch("/sign/signin", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        memberId: document.getElementById("signinId").value,
        pwd: document.getElementById("signinPwd").value,
      }),
    });

    if (response.ok) {
      const token = await response.text();
      const isTempPassword = response.headers.get("X-Temp-Password");
      console.log("임시 비밀번호 여부:", isTempPassword);

      document.cookie = `accessToken=${token}; path=/; max-age=604800; secure`;

      if (isTempPassword === "true") {
        console.log("비밀번호 변경 페이지로 이동");
        window.location.href = "/sign/forgotPasswordChange";
      } else {
        location.href = "/post/main";
      }
    } else {
      const errorMessage = await response.text();
      alert(errorMessage);
    }
  } catch (error) {
    alert(error.message);
  }
});

// 로그인 스크립트 끗

// 비동기 방식으로 토큰 전송
// async function sendTokenAsync() {
//   try {
//     const token = document.cookie
//       .split("; ")
//       .find((row) => row.startsWith("accessToken="))
//       ?.split("=")[1];
//
//     if (!token) {
//       alert("토큰이 없습니다. 로그인이 필요합니다.");
//       return;
//     }
//
//     const response = await fetch("/sign/cookie-test", {
//       method: "POST",
//       headers: {
//         "Content-Type": "application/x-www-form-urlencoded",
//       },
//       body: `accessToken=${token}`,
//     });
//
//     if (response.ok) {
//       const result = await response.text();
//       alert("토큰 전송 성공\n" + result);
//     } else {
//       throw new Error("토큰 전송 실패");
//     }
//   } catch (error) {
//     alert(error.message);
//   }
// }
