"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.processColorProps = processColorProps;
exports.guid = guid;
var react_native_1 = require("react-native");
function processColorProps(props, name) {
    if (props[name]) {
        /* eslint-disable no-param-reassign */
        // @ts-ignore
        props[name] = (0, react_native_1.processColor)(props[name]);
        /* eslint-enable no-param-reassign */
    }
}
function guid() {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }
    return "".concat(s4()).concat(s4(), "-").concat(s4(), "-").concat(s4(), "-").concat(s4(), "-").concat(s4()).concat(s4()).concat(s4());
}
//# sourceMappingURL=index.js.map