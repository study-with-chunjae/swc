// Update the slider position and active indicator
function updateSlider(currentIndex,slider) {
    const offset = -currentIndex * 100; // Each slide is 100% wide
    slider.style.transform = `translateX(${offset}%)`;

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
function createIndicators(indicatorsContainer,totalSlides,currentIndex,slider) {
    indicatorsContainer.innerHTML = ""; // Clear existing indicators
    for (let i = 0; i < totalSlides; i++) {
        const indicator = document.createElement("div");
        indicator.classList.add("indicator");
        if (i === currentIndex) indicator.classList.add("active"); // Highlight the first indicator
        // Add click event to indicators
        indicator.addEventListener("click", function () {
            currentIndex = i; // Update the current index
            updateSlider(currentIndex,slider);
        });
        indicatorsContainer.appendChild(indicator);
    }
}


function initSliders(){
    const slider = document.querySelector("#slider");
    const slides = document.querySelectorAll(".learning-card");
    const indicatorsContainer = document.querySelector(".slider-indicators");

    let currentIndex = 0; // Start at the first slide
    const totalSlides = slides.length;
    createIndicators(indicatorsContainer,totalSlides,currentIndex,slider);
}

async function getMainPosts(element) {
    const createdAt = element.getAttribute("data-date-format");
    try {
        const response = await fetch(`/posts/my-posts/main-posts/${createdAt}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            console.log("response not ok");
            const error = await response.json();
            console.log(error.message);
            alert(error.message);
            return;
        }

        console.log("response ok");
        const result = await response.json();
        console.log(result);
        let slider = document.querySelector("#slider");
        slider.innerHTML = ''; // 기존 내용을 초기화

        for (let mainPost of result.data) {
            slider.innerHTML += `
				<article class="learning-card">
					<div class="thumbnail">
						<img src="${mainPost.image}" alt="Thumbnail">
						<p class="category">${mainPost.topics}</p>
						<p class="category">${mainPost.hashtag}</p>
					</div>
					<div class="info">
						<h3 class="title">${mainPost.title}</h3>
						<p class="description">${mainPost.content}</p>
						<div class="shared-by">
            `;
            for (let share of mainPost.shares) {
                slider.innerHTML += `${share.member.name}<br>`;
            }
            slider.innerHTML += `
						</div>
					</div>
                </article>
            `;
        }
        initSliders();
    } catch (error) {
        console.log(error);
        location.href = "/post/main?createdAt=" + createdAt;
    }
}

const weekdays = ["일", "월", "화", "수", "목", "금", "토"];
const weekdayList = document.querySelector("#weekday-list"); //날짜 들어갈 ul
const todayDisplay = document.querySelector("#today"); //
const upButton = document.querySelector(".arrow.up");
const downButton = document.querySelector(".arrow.down");// 기준 날짜

// Function to calculate the week number of a given date
function getWeekNumber(date) {
    const firstDay = new Date(date.getFullYear(), date.getMonth(), 1); // 해당 월의 첫째 날
    const firstDayOfWeek = firstDay.getDay(); // 첫째 날의 요일
    const offsetDate = date.getDate() + firstDayOfWeek; // 기준 날짜까지의 오프셋
    return Math.ceil(offsetDate / 7); // 몇째 주인지 계산
}

// Function to render weekdays based on the base date
function renderWeekdays(inputDate) {
    weekdayList.innerHTML = "";
    for (let i = 0; i < 7; i++) {
        const date = new Date(inputDate);
        date.setDate(inputDate.getDate() + i); // Calculate each date in the 7-day range
        const dayIndex = date.getDay();
        const month = date.getMonth() + 1; // Month is zero-based
        const day = date.getDate();
        const li = document.createElement("li");
        li.textContent = `${month}월 ${day}일(${weekdays[dayIndex]})`;
        li.setAttribute("data-date", `${month}월 ${day}일`); // Store the date as a data attribute
        //데이터 불러올 날짜 형식의 값 data attribute에 저장(yyyy-MM-dd)
        li.setAttribute("data-date-format", `${date.getFullYear()}-${String(month).padStart(2,'0')}-${String(day).padStart(2,'0')}`);
        li.classList.add("post-date");
        li.setAttribute("data-month", `${month}월`); // Store the month as a data attribute
        // Highlight today if within the current week range
        if (date.toDateString() === new Date().toDateString()) {
            li.classList.add("today");
            // Add calendar icon for today's date
            const calendarIcon = document.createElement("span");
            calendarIcon.textContent = " 📅";
            calendarIcon.classList.add("calendar-icon");
            li.appendChild(calendarIcon);
            // Display today's date initially
            const weekNumber = getWeekNumber(date); // Calculate the week number for today
            todayDisplay.innerHTML = `<b>${month}월 ${day}일(${weekdays[dayIndex]})</b> <br> (${month}월, ${weekNumber}주) 📅`;
        }
        // Add event listener to display clicked date, month, and week info
        li.addEventListener("click", function () {
            const clickedDate = new Date(inputDate);
            clickedDate.setDate(inputDate.getDate() + i); // Adjust date for each list item
            const weekNumber = getWeekNumber(clickedDate); // Calculate the week number
            todayDisplay.innerHTML = `<b>${this.getAttribute("data-date")}(${this.textContent})</b> <br> (${this.getAttribute("data-month")}, ${weekNumber}주)`;
        });
        weekdayList.appendChild(li);
    }
    let postDate = document.querySelectorAll(".post-date");
    console.log(postDate);
    for (let i of postDate) {
        i.addEventListener("click", async (event) => {
            const createdAt = event.target.getAttribute("data-date-format");
            await getMainPosts(event.target);
        });
    }
}

document.addEventListener("DOMContentLoaded",(e)=>{
    renderWeekdays(baseDate);
});

upButton.addEventListener("click", function () {
    baseDate.setDate(baseDate.getDate() - 7); // Move 7 days back
    renderWeekdays(baseDate);
});

downButton.addEventListener("click", function () {
    baseDate.setDate(baseDate.getDate() + 7); // Move 7 days forward
    renderWeekdays(baseDate);
});

let postDate = document.querySelectorAll(".post-date");
for(let i of postDate){
    i.addEventListener("click",(e)=>{
        const inputDate = new Date(e.target.getAttribute("data-date-format"));
        renderWeekdays(inputDate);
    });
}