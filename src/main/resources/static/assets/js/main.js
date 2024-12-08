document.addEventListener("DOMContentLoaded", function () {
    const weekdays = ["일", "월", "화", "수", "목", "금", "토"];
    const weekdayList = document.getElementById("weekday-list");
    const todayDisplay = document.getElementById("today");
    const upButton = document.querySelector(".arrow.up");
    const downButton = document.querySelector(".arrow.down");

    let baseDate = new Date(); // Reference date for calculating the 7-day range

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
            // Highlight today if within the current week range
            if (date.toDateString() === new Date().toDateString()) {
                li.classList.add("today");

                // Add calendar icon for today's date
                const calendarIcon = document.createElement("span");
                calendarIcon.textContent = " 📅";
                calendarIcon.classList.add("calendar-icon");
                // calendarIcon.addEventListener("click", () => {
                //     alert("달력 모달 열기"); // Replace this with actual modal logic if needed
                // });
                li.appendChild(calendarIcon);

                // Display today's date initially
                todayDisplay.innerHTML = `<b>${weekdays[dayIndex]}: ${month}월 ${day}일</b> 📅`;
            }

            // Add event listener to display clicked date
            li.addEventListener("click", function () {
                todayDisplay.innerHTML = `<b>${this.textContent}: ${this.getAttribute("data-date")}</b>`;
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
