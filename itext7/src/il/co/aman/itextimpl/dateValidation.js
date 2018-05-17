/*
 * Only supports dates in the 20th and 21st centuries. 
 * May 2013 - added email validation
 */
var ddmmyyyyregex = /^([1-9]|0[1-9]|[12][0-9]|3[01])[- /.]([1-9]|0[1-9]|1[012])[- /.](19\d\d|20\d\d)$/;
var mmddyyyyregex = /^([1-9]|0[1-9]|1[012])[- /.]([1-9]|0[1-9]|[12][0-9]|3[01])[- /.](19\d\d|20\d\d)$/;
var yyyymmddregex = /^(19\d\d|20\d\d)[- /.]([1-9]|0[1-9]|1[012])[- /.]([1-9]|0[1-9]|[12][0-9]|3[01])$/;

var emailregex = /^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/;

var VALID = 0;
var WRONG_FORMAT = -1;
var INVALID_DATE = -2;

function it3xt1Mpl___checkFormat(isdate, format) {
    // returns 0 if the date is valid, -1 if the format is wrong, -2 if the format is right but the date does not exist (e.g. 31/02/2010)
    var res = WRONG_FORMAT;
    var match = null;
    switch(format) {
        case "ddmmyyyy":
            match = ddmmyyyyregex.exec(isdate);
            if (match != null) {
                res = it3xt1Mpl___valid(match[1], match[2], match[3])? VALID: INVALID_DATE;
            }
            break;
        case "mmddyyyy":
            match = mmddyyyyregex.exec(isdate);
            if (match != null) {
                res = it3xt1Mpl___valid(match[2], match[1], match[3])? VALID: INVALID_DATE;
            }
            break;
        case "yyyymmdd":
            match = yyyymmddregex.exec(isdate);
            if (match != null) {
                res = it3xt1Mpl___valid(match[3], match[2], match[1])? VALID: INVALID_DATE;
            }
            break;
        case "any":
            match = ddmmyyyyregex.exec(isdate);
            if (match == null) {
                res = WRONG_FORMAT;
            } else {
                res = it3xt1Mpl___valid(match[1], match[2], match[3])? VALID: INVALID_DATE;
            }
            if (res == WRONG_FORMAT) {
                match = mmddyyyyregex.exec(isdate);
                if (match == null) {
                    res = WRONG_FORMAT;
                } else {
                    res = it3xt1Mpl___valid(match[2], match[1], match[3])? VALID: INVALID_DATE;
                }
                if (res == WRONG_FORMAT) {
                    match = yyyymmddregex.exec(isdate);
                    if (match == null) {
                        res = WRONG_FORMAT;
                    } else {
                        res =  it3xt1Mpl___valid(match[3], match[2], match[1])? VALID: INVALID_DATE;
                    }
                }
            }
            break;
    }
    return res;
}

function it3xt1Mpl___valid(day, month, year) {
    var res = true;
    if (day == 31 && (month == 2 || month == 4 || month == 6 || month == 9 || month == 11)) {
        res = false;
    } else if (day == 30 && month == 2) {
        res = false;
    } else if (day == 29 && month == 2 && (year % 400 != 0 && (year % 100 == 0 || year % 4 != 0))) {
        res = false;
    }
    return res;
}

function it3xt1Mpl___checkDate(format) {
    var res = it3xt1Mpl___checkFormat(event.value, format);
    if (res == WRONG_FORMAT || res == INVALID_DATE) {
        var wrong = 'Invalid date: ' + event.value + '.';
        if (res == WRONG_FORMAT) {
            wrong += '\nRequired format: ' + format + '.';
        }
        app.alert(wrong);
        event.value = '';
        //this.getField(event.target.name).setFocus();
    }
}

function it3xt1Mpl___checkEmail() {
    var val = event.value.toLowerCase();
    var match = emailregex.exec(val);
    if (match == null) {
        app.alert("Invalid email address: " + event.value + ".");
        event.value = '';
    }
}
