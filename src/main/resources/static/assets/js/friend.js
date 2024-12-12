    // 친구 요청 수락 함수
    window.acceptFriendRequest = function(element) {
        const friendId = element.getAttribute("data-idx");
        fetch(`/friend/accept/${friendId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(function(response) {
                return response.json().then(function(data) {
                    if (!response.ok) {
                        throw new Error(data.message || '친구 요청에 실패했습니다.');
                    }
                    return data;
                });
            })
            .then(function(data) {
                alert(data.message);
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
            .then(function(response) {
                return response.json().then(function(data) {
                    if (!response.ok) {
                        throw new Error(data.message || '친구 요청에 실패했습니다.');
                    }
                    return data;
                });
            })
            .then(function(data) {
                alert(data.message);
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
            .then(function(response) {
                return response.json().then(function(data) {
                    if (!response.ok) {
                        throw new Error(data.message || '친구 요청에 실패했습니다.');
                    }
                    return data;
                });
            })
            .then(function(data) {
                alert(data.message);
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