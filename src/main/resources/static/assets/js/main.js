document.addEventListener("DOMContentLoaded", function () {
    const weekdays = ["일", "월", "화", "수", "목", "금", "토"];
    const weekdayList = document.getElementById("weekday-list");
    const todayDisplay = document.getElementById("today");
    const upButton = document.querySelector(".arrow.up");
    const downButton = document.querySelector(".arrow.down");

    let baseDate = new Date(); // 기준 날짜

    // Function to calculate the week number of a given date
    function getWeekNumber(date) {
        const firstDay = new Date(date.getFullYear(), date.getMonth(), 1); // 해당 월의 첫째 날
        const firstDayOfWeek = firstDay.getDay(); // 첫째 날의 요일
        const offsetDate = date.getDate() + firstDayOfWeek; // 기준 날짜까지의 오프셋
        return Math.ceil(offsetDate / 7); // 몇째 주인지 계산
    }

    // Function to render weekdays based on the base date
    function renderWeekdays() {
        weekdayList.innerHTML = "";

        for (let i = 0; i < 7; i++) {
            const date = new Date(baseDate);
            date.setDate(baseDate.getDate() + i); // Calculate each date in the 7-day range

            const dayIndex = date.getDay();
            const month = date.getMonth() + 1; // Month is zero-based
            const day = date.getDate();

            const li = document.createElement("li");
            li.textContent = weekdays[dayIndex];
            li.setAttribute("data-date", `${month}월 ${day}일`); // Store the date as a data attribute

            //강감찬 추가
            li.setAttribute("data-date-format", `${date.getFullYear()}-${String(month).padStart(2,'0')}-${String(day).padStart(2,'0')}`);
            li.classList.add("post-date");
            //강감찬 추가

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
                todayDisplay.innerHTML = `<b>${weekdays[dayIndex]}: ${month}월 ${day}일</b> <br> (${weekNumber}주, ${month}월) 📅`;

            }

            // Add event listener to display clicked date, month, and week info
            li.addEventListener("click", function () {
                const clickedDate = new Date(baseDate);
                clickedDate.setDate(baseDate.getDate() + i); // Adjust date for each list item
                const weekNumber = getWeekNumber(clickedDate); // Calculate the week number

                todayDisplay.innerHTML = `<b>${this.textContent}: ${this.getAttribute("data-date")}</b> <br> (${this.getAttribute("data-month")}, ${weekNumber}주)`;

            });

            weekdayList.appendChild(li);
        }
        //강감찬추가
        postDate = document.querySelectorAll(".post-date");
        console.log(postDate);
        for (let i of postDate) {
            i.addEventListener("click", (event) => {
                const createdAt = event.target.getAttribute("data-date-format");
                location.href="/post/main?createdAt=" + createdAt;
            });
        }
        //강감찬추가
    }

    // Event listeners for arrow buttons
    upButton.addEventListener("click", function () {
        baseDate.setDate(baseDate.getDate() - 7); // Move 7 days back
        renderWeekdays();
    });

    downButton.addEventListener("click", function () {
        baseDate.setDate(baseDate.getDate() + 7); // Move 7 days forward
        renderWeekdays();
    });

    // Initial render
    renderWeekdays();
});



document.addEventListener("DOMContentLoaded", function () {
    const slider = document.querySelector(".slider");
    const slides = document.querySelectorAll(".learning-card");
    const indicatorsContainer = document.querySelector(".slider-indicators");

    let currentIndex = 0; // Start at the first slide
    const totalSlides = slides.length;

    // Create indicators
    function createIndicators() {
        indicatorsContainer.innerHTML = ""; // Clear existing indicators
        for (let i = 0; i < totalSlides; i++) {
            const indicator = document.createElement("div");
            indicator.classList.add("indicator");
            if (i === currentIndex) indicator.classList.add("active"); // Highlight the first indicator

            // Add click event to indicators
            indicator.addEventListener("click", function () {
                currentIndex = i; // Update the current index
                updateSlider();
            });

            indicatorsContainer.appendChild(indicator);
        }
    }

    // Update the slider position and active indicator
    function updateSlider() {
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

    // Initial setup
    createIndicators();
    updateSlider();

    // Auto-slide every 5 seconds (optional)
    // setInterval(function () {
    //     currentIndex = (currentIndex + 1) % totalSlides;
    //     updateSlider();
    // }, 5000);
});
