function emptyAndPatternCheck(입력값, $출력할곳, pattern, emptyErrorMessage, patternErrorMessage) {
    $출력할곳.text('');
    if(입력값 == '') {
        $출력할곳.text(emptyErrorMessage);
        return false;
    } else if(pattern.test(입력값) == false) {
        $출력할곳.text(patternErrorMessage);
        return false;
    }
    return true;
}

// 이름 검증 함수
function nameCheck() {
    const pattern = /^.{2,}$/;  // 최소 2글자 이상
    const value = $('#name').val();
    const $nameMsg = $('#name-msg');
    return emptyAndPatternCheck(value, $nameMsg, pattern, '이름을 입력하세요', '이름은 최소 2글자 이상이어야 합니다');
}

// 전화번호 검증 함수
function phoneCheck() {
    const pattern = /^[0-9]{10,11}$/;  // 10자리 또는 11자리 숫자
    const value = $('#phone').val();
    const $phoneMsg = $('#phone-msg');
    return emptyAndPatternCheck(value, $phoneMsg, pattern, '전화번호를 입력하세요', '유효한 전화번호 형식이 아닙니다');
}

function birthdayCheck() {
    const pattern = /^[0-9]{4}-[0-9]{2}-[0-9]{2}$/;
    const value = $('#birthday').val();
    const $birthdayMsg = $('#birthday-msg');
    return emptyAndPatternCheck(value, $birthdayMsg, pattern, '생일을 입력하세요', '정확한 생일을 입력하세요');
}

function emailCheck() {
    const pattern = /^[0-9a-zA-Z]([--.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([--.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;
    const value = $('#email').val();
    const $emailMsg = $('#email-msg');
    return emptyAndPatternCheck(value, $emailMsg, pattern, '이메일을 입력하세요', '정확한 이메일을 입력하세요');
}

function passwordCheck(id) {
    const pattern = /^[A-Za-z0-9]{6,10}$/;
    const value = $('#' + id).val();
    const $passwordMsg = $('#' + id + '-msg');
    return emptyAndPatternCheck(value, $passwordMsg, pattern, '비밀번호를 입력하세요', '비밀번호는 영숫자 6~10자입니다');
}

function password2Check() {
    const password = $('#new-password').val();
    const password2 = $('#password2').val();
    const $password2Msg = $('#password2-msg');
    $password2Msg.text('');
    if(password == '') {
        $password2Msg.text('비밀번호를 다시 입력하세요');
        return false;
    } else if(password != password2) {
        $password2Msg.text('비밀번호가 일치하지 않습니다');
        return false;
    }
    return true;
}

async function usernameCheck(availableCheck = true) {
    const pattern = /^[a-z0-9]{6,10}$/;
    const value = $('#username').val();
    const $usernameMsg = $('#username-msg');
    const result = emptyAndPatternCheck(value, $usernameMsg, pattern, '아이디를 입력하세요', '아이디는 소문자와 숫자 6-10자입니다');    
    if(result == false)
        return false;
    if(availableCheck == false)
        return result;
    
    try {
        await $.ajax("/members/username/available?username=" + value);
        return true;
    } catch(err) {
        console.log(err);
        $usernameMsg.text('사용중인 아이디입니다');
        return false;
    }
}
