// Update the slider position and active indicator
function updateSlider(currentIndex, slider) {
  const offset = -currentIndex * 601;
  slider.style.transform = `translateX(${offset}px)`;

  // Update active indicator
  const indicators = document.querySelectorAll(".indicator");
  indicators.forEach((indicator, index) => {
    if (index === currentIndex) {
      indicator.classList.add("active");
    } else {
      indicator.classList.remove("active");
    }
  });
}

// Create indicators
function createIndicators(
  indicatorsContainer,
  totalSlides,
  currentIndex,
  slider
) {
  indicatorsContainer.innerHTML = ""; // Clear existing indicators
  for (let i = 0; i < totalSlides; i++) {
    const indicator = document.createElement("div");
    indicator.classList.add("indicator");
    if (i === currentIndex) indicator.classList.add("active"); // Highlight the first indicator
    // Add click event to indicators
    indicator.addEventListener("click", function () {
      currentIndex = i; // Update the current index
      updateSlider(currentIndex, slider);
    });
    indicatorsContainer.appendChild(indicator);
  }
}

function initSliders() {
  const slider = document.querySelector("#slider");
  const slides = document.querySelectorAll(".learning-card");
  const indicatorsContainer = document.querySelector(".slider-indicators");

  let currentIndex = 0; // Start at the first slide
  const totalSlides = slides.length;
  createIndicators(indicatorsContainer, totalSlides, currentIndex, slider);
}

async function getMainPosts(element) {
    const createdAt = element.getAttribute("data-date-format");
    console.log(createdAt);
    try {
        const response = await fetch(`/posts/my-posts/main-posts/${createdAt}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });
        let slider = document.querySelector("#slider");
        if (!response.ok) {
            console.log("response not ok");
            const error = await response.json();
            console.log(error.message);
            slider.innerHTML = '';
            return;
        }
        console.log("response ok");
        const result = await response.json();
        console.log(result);
        slider.innerHTML = ''; // 기존 내용을 초기화
        const postList = result.data;
        if(postList.length > 0) {
            for (let mainPost of result.data) {
                slider.innerHTML += `
				<article class="learning-card">
					<div class="thumbnail">
						<img src="${mainPost.image==null?'/upload/images/default_image.jpg':mainPost.image}" alt="Thumbnail">
						<p class="category">${mainPost.topics}</p>
						<p class="category">${mainPost.hashtag}</p>
					</div>
					<div class="info">
						<h3 class="title">${mainPost.title}</h3>
						<p class="description">${mainPost.content}</p>
						<div class="shared-by">
            `;
                for (let share of mainPost.shares) {
                    slider.innerHTML += `${share}<br>`;
                }
                slider.innerHTML += `
						</div>
						<div class="thumbUps">${mainPost.thumbUps}</div>
					</div>
                </article>
            `;
            }
        }else{
            slider.innerHTML += `
				<article class="learning-card">
					<div class="info">
						<h3 class="title">등록된 오늘의학습이 없습니다.</h3>
					</div>
                </article>
            `;
        }
        initSliders();
    } catch (error) {
        console.log(error);
        //location.href = "/post/main?createdAt=" + createdAt;
    }
}

const weekdays = ["일", "월", "화", "수", "목", "금", "토"];
const weekdayList = document.querySelector("#weekday-list"); //날짜 들어갈 ul
const todayDisplay = document.querySelector("#today"); //
const upButton = document.querySelector(".arrow.up");
const downButton = document.querySelector(".arrow.down"); // 기준 날짜

// Function to calculate the week number of a given date
function getWeekNumber(date) {
  const firstDay = new Date(date.getFullYear(), date.getMonth(), 1); // 해당 월의 첫째 날
  const firstDayOfWeek = firstDay.getDay(); // 첫째 날의 요일
  const offsetDate = date.getDate() + firstDayOfWeek; // 기준 날짜까지의 오프셋
  return Math.ceil(offsetDate / 7); // 몇째 주인지 계산
}

function getMondayOfWeek(date) {
  const day = date.getDay(); // 일=0, 월=1, ... 토=6
  // day가 0(일요일)일 경우 월요일은 date에서 -6일,
  // 그 외에는 (1 - day)일 이동해 월요일을 구한다.
  const diff = day === 0 ? -6 : 1 - day;
  const monday = new Date(date);
  monday.setDate(date.getDate() + diff);
  return monday;
}

// 기존 today 하이라이트 로직 + selected 클래스 추가
function renderWeekdays(inputDate) {
  weekdayList.innerHTML = "";
  // 해당 주의 월요일부터 시작
  const mondayOfWeek = getMondayOfWeek(inputDate);
  baseDate = mondayOfWeek;
  for (let i = 0; i < 7; i++) {
    const date = new Date(mondayOfWeek);
    date.setDate(mondayOfWeek.getDate() + i);
    const dayIndex = date.getDay();
    const month = date.getMonth() + 1;
    const day = date.getDate();

    const li = document.createElement("li");
    li.textContent = `${month}월 ${day}일(${weekdays[dayIndex]})`;
    li.setAttribute("data-date", `${month}월 ${day}일`);
    li.setAttribute(
      "data-date-format",
      `${date.getFullYear()}-${String(month).padStart(2, "0")}-${String(
        day
      ).padStart(2, "0")}`
    );
    li.classList.add("post-date");
    li.setAttribute("data-month", `${month}월`);

    // 오늘 날짜 하이라이트
    if (date.toDateString() === new Date().toDateString()) {
      const calendarIcon = document.createElement("span");
      calendarIcon.textContent = " 📅";
      calendarIcon.classList.add("calendar-icon");
      li.appendChild(calendarIcon);
    }

    // 날짜 클릭 시 selected 스타일 추가
    li.addEventListener("click", function () {
      // 모든 post-date에서 selected 클래스 제거
      document
        .querySelectorAll(".post-date")
        .forEach((el) => el.classList.remove("selected"));
      this.classList.add("selected");
      const clickedDate = new Date(mondayOfWeek);
      clickedDate.setDate(mondayOfWeek.getDate() + i);
      const weekNumber = getWeekNumber(clickedDate);
      todayDisplay.innerHTML = `<b>${this.getAttribute(
        "data-date"
      )}</b> <br> (${this.getAttribute("data-month")}, ${weekNumber}주)`;
    });

    weekdayList.appendChild(li);
    initSliders();
  }

  let postDate = document.querySelectorAll(".post-date");
  for (let i of postDate) {
    i.addEventListener("click", async (event) => {
      await getMainPosts(event.target);
    });
  }
}

// 나머지 코드(이벤트 핸들러, init 등)는 기존과 동일하게 유지
// baseDate를 기준으로 최초 렌더
document.addEventListener("DOMContentLoaded", (e) => {
  renderWeekdays(baseDate);
  document.querySelectorAll(".post-date").forEach((el) => {
    if (
      el.getAttribute("data-date-format") ===
      new Date().toISOString().slice(0, 10)
    ) {
      el.classList.add("selected");
    }
  });
});

upButton.addEventListener("click", function () {
  baseDate.setDate(baseDate.getDate() - 7);
  renderWeekdays(baseDate);
});

downButton.addEventListener("click", function () {
  baseDate.setDate(baseDate.getDate() + 7);
  renderWeekdays(baseDate);
});
