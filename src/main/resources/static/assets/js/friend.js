document.addEventListener('DOMContentLoaded', function() {
    const keywordInput = document.getElementById('keyword');
    const searchResultsList = document.getElementById('search-results');
    const loadMoreButton = document.getElementById('load-more-button');
    let debounceTimeout = null;
    let currentPage = 0;
    let currentKeyword = '';

    // 초기 "더보기" 버튼 숨기기
    loadMoreButton.style.display = 'none';

    keywordInput.addEventListener('input', function() {
        const keyword = this.value.trim();
        currentKeyword = keyword;
        currentPage = 0; // 새로운 검색 시 페이지 초기화

        // 디바운싱: 300ms 동안 입력이 없으면 검색 요청
        if (debounceTimeout) {
            clearTimeout(debounceTimeout);
        }

        debounceTimeout = setTimeout(() => {
            if (keyword.length === 0) {
                searchResultsList.innerHTML = '';
                loadMoreButton.style.display = 'none';
                return;
            }

            // 검색 초기화
            searchResultsList.innerHTML = '';
            fetchFriends(keyword, currentPage);
        }, 300);
    });

    function fetchFriends(keyword, page) {
        fetch(`/friend/search?keyword=${encodeURIComponent(keyword)}&limit=5&page=${page}`)
            .then(response => response.json())
            .then(data => {
                displaySearchResults(data);
                // "더보기" 버튼 표시 여부 결정
                if (data.length === 5) { // limit=5
                    loadMoreButton.style.display = 'block';
                } else {
                    loadMoreButton.style.display = 'none';
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }

    function displaySearchResults(members) {
        if (currentPage === 0) {
            searchResultsList.innerHTML = ''; // 검색 초기화
        }

        if (members.length === 0 && currentPage === 0) {
            const li = document.createElement('li');
            li.textContent = '검색 결과가 없습니다.';
            searchResultsList.appendChild(li);
            loadMoreButton.style.display = 'none'; // 결과가 없으므로 "더보기" 숨김
            return;
        }

        // 멤버 추가
        members.forEach(member => {
            const li = document.createElement('li');
            const span = document.createElement('span');
            span.textContent = `${member.memberId} (${member.name})`;

            const requestButton = document.createElement('button');
            requestButton.textContent = '친구 신청';
            requestButton.className = 'button button-request';
            requestButton.onclick = () => sendFriendRequest(member.memberId);

            li.appendChild(span);
            li.appendChild(requestButton);
            searchResultsList.appendChild(li);
        });

        // "더보기" 버튼 재배치
        if (members.length === 5) { // limit=5 기준
            loadMoreButton.style.display = 'block';
            searchResultsList.appendChild(loadMoreButton);
        } else {
            loadMoreButton.style.display = 'none';
        }
    }

    function sendFriendRequest(receiverId) {
        fetch('/friend/request', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ receiver: receiverId })
        })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => { throw new Error(text) });
                }
                return response.text();
            })
            .then(message => {
                alert(message);
                // 친구 요청을 보낸 사용자를 검색 결과 목록에서 제거
                const buttons = document.querySelectorAll(`button.button-request[onclick="sendFriendRequest('${receiverId}')"]`);
                buttons.forEach(button => {
                    button.parentElement.remove();
                });
            })
            .catch(error => {
                console.error('Error:', error);
                alert(error.message || '친구 요청에 실패했습니다.');
            });
    }

    // 친구 요청 수락 함수
    window.acceptFriendRequest = function(element) {
        const friendId = element.getAttribute("data-idx");
        fetch(`/friend/accept/${friendId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => { throw new Error(text) });
                }
                return response.text();
            })
            .then(message => {
                alert(message);
                // 해당 친구 요청을 리스트에서 제거
                //const button = document.querySelector(`button.button-accept[onclick="acceptFriendRequest(${element})"]`);
                //if (button) {
                //    element.parentElement.parentElement.remove();
                //}
                location.reload();
            })
            .catch(error => {
                console.error('Error:', error);
                alert(error.message || '친구 요청 수락에 실패했습니다.');
            });
    };

    // 친구 요청 거절 함수
    window.rejectFriendRequest = function(element) {
        const friendId = element.getAttribute("data-idx");
        fetch(`/friend/reject/${friendId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => { throw new Error(text) });
                }
                return response.text();
            })
            .then(message => {
                alert(message);
                // 해당 친구 요청을 리스트에서 제거
                //const button = document.querySelector(`button.button-reject[onclick="rejectFriendRequest(${element})"]`);
                //if (button) {
                //element.parentElement.parentElement.remove();
                //}
                location.reload();
            })
            .catch(error => {
                console.error('Error:', error);
                alert(error.message || '친구 요청 거절에 실패했습니다.');
            });
    };

    // 친구 삭제 함수
    window.deleteFriend = function(friendId) {
        if (!confirm('정말 친구를 삭제하시겠습니까?')) {
            return;
        }

        fetch(`/friend/delete/${friendId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => { throw new Error(text) });
                }
                return response.text();
            })
            .then(message => {
                alert(message);
                // 해당 친구를 목록에서 제거
                const button = document.querySelector(`button.button-delete[onclick="deleteFriend(${friendId})"]`);
                if (button) {
                    button.parentElement.remove();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert(error.message || '친구 삭제에 실패했습니다.');
            });
    };

    // "더보기" 버튼 클릭 시 다음 페이지 로드
    loadMoreButton.addEventListener('click', function() {
        currentPage += 1;
        fetchFriends(currentKeyword, currentPage);
    });
});