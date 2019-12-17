'use strict';

module.exports = function() {

    var self = this;

    self.convertTable2Object = function(table, apickli) {
        var object = {};
        var entries = table.hashes();
        for (var i = 0; i < entries.length; i++) {
            object[entries[i].name] = apickli.replaceVariables(entries[i].value);
        }
        //console.log(entries);
        return object;
    }
}