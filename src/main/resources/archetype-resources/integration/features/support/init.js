/* jshint node:true */
'use strict';

var apickli = require('apickli');
var config = require('../../config.json');

var northboundUrl = config.parameters.northboundUrl;
var basepath = config.parameters.basepath;

console.log('api parameters: [' + northboundUrl + ', ' + basepath + ']');

module.exports = function() {
    // cleanup before every scenario
    this.Before(function(scenario, callback) {
        console.log('Before scenario hook');
        var urlParts = northboundUrl.split("://");
        this.apickli = new apickli.Apickli(urlParts[0], urlParts[1] + basepath);
        callback();
    });
};
