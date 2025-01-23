(function() {

    /**
     * Initialize
     */
    function init() {
        // Register event listeners
        $('login-btn').addEventListener('click', login);
        $('createAccPage-btn').addEventListener('click', createAccountPage);
        $('createAcc-btn').addEventListener('click', createAccount);
        $('logout-link').addEventListener('click', logout);
        $('back-to-login-btn').addEventListener('click', backToLogIn);
        $('add-back-to-Home-btn').addEventListener('click', backToHomePage);
        $('log-back-to-Home-btn').addEventListener('click', backToHomePage);
        $('download-btn').addEventListener('click', download);
        $('add-btn').addEventListener('click', addMachinePage);
        $('add-machine-btn').addEventListener('click', addMachine);
        $('log-btn').addEventListener('click', logPage);
        $('key-btn').addEventListener('click', generateKey);
        $('welcome-msg').textContent = '';
        

    }
    
    // -----------------------------------
    // Helper Functions
    // -----------------------------------

    function activeBtn(btnId) {
        var btns = document.getElementsByClassName('main-nav-btn');

        // deactivate all navigation buttons
        for (var i = 0; i < btns.length; i++) {
            btns[i].className = btns[i].className.replace(/\bactive\b/, '');
        }

        // activate the one that has id = btnId
        var btn = $(btnId);
        btn.className += ' active';
    }
    
    function deactivateButton(btnId) {
    var btns = document.getElementsByClassName('main-nav-btn');

    // Deactivate all navigation buttons
    for (var i = 0; i < btns.length; i++) {
        btns[i].classList.remove('active');
    }
}

	function showLoadingMessage(msg) {
        var itemList = $('item-list');
        itemList.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i> ' +
            msg + '</p>';
    }
    
    function hideLoadingMessage() {
    	var itemList = $('#item-list'); // Use $('#') to select by ID
    	itemList.empty(); // Clear the HTML content of the itemList element
	}

    function showWarningMessage(msg) {
        var itemList = $('item-list');
        itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-triangle"></i> ' +
            msg + '</p>';
    }

    function showErrorMessage(msg) {
        var itemList = $('item-list');
        itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-circle"></i> ' +
            msg + '</p>';
    }
    
    function $(tag, options) {
        if (!options) {
            return document.getElementById(tag);
        }

        var element = document.createElement(tag);

        for (var option in options) {
            if (options.hasOwnProperty(option)) {
                element[option] = options[option];
            }
        }

        return element;
    }

    function hideElement(element) {
        element.style.display = 'none';
    }

    function showElement(element, style) {
        var displayStyle = style ? style : 'block';
        element.style.display = displayStyle;
    }

    function ajax(method, url, data, callback, errorHandler) {
        var xhr = new XMLHttpRequest();

        xhr.open(method, url, true);

        xhr.onload = function() {
            if (xhr.status === 200) {
                callback(xhr.responseText);
            } else {
                errorHandler();
            }
        };

        xhr.onerror = function() {
            console.error("The request couldn't be completed.");
            errorHandler();
        };

        if (data === null) {
            xhr.send();
        } else {
            xhr.setRequestHeader("Content-Type", "application/json;charset=utf-8");
            xhr.send(data);
        }
    }
    
    function resetFormValue() {
		$('username').value = "";
		$('password').value = "";
		$('username-create').value = "";
		$('password-create').value = "";
		$('fname').value = "";
		$('lname').value = "";
		$('key-create').value = "";
		$('name').value = "";
		$('ip').value = "123.456.78.90";
		$('hr').value = "Please Enter In 24 Hour (0~23)";
		$('min').value = "(0~59)";
		$('add-error').innerHTML = '';
		$('create-error').innerHTML = '';
		$('login-error').innerHTML = '';
	}
    
	// --------------
	// Page Switching Manipulation
	// ---------------

	function createAccountPage() {
		hideElement($('login-form'));
		showElement($('create-form'));
		resetFormValue();
	}
	
	function backToLogIn() {
		showElement($('login-form'));
		hideElement($('create-form'));
		resetFormValue();
	}
	
	function addMachinePage() {
		activeBtn('add-btn');
		hideElement($('item-list'));
		hideElement($('logDropDown'));
		resetFormValue();
		showElement($('add-form'));
	}
	
	function backToHomePage() {
		hideElement($('add-form'));
		hideElement($('logDropDown'));
		deactivateButton('add-btn');
		deactivateButton('log-btn');
		loadMachines();
		showElement($('item-list'));
	}
	
	function logPage() {
		activeBtn('log-btn');
		hideElement($('item-list'));
		hideElement($('add-form'));
		populateDateDropdown();
		showElement($('logDropDown'));
	}
	
	
	// ----------------
    // functions calling AJAX API
    // ----------------
    
    function generateKey() {
		var req = JSON.stringify({});
		
		var url = './GenerateKey';
		
		ajax('POST', url, req, function(responseText) {
        	var response = JSON.parse(responseText);

    	    if (response.keyGeneration === 'SUCCESS') {
            	console.log('Key generation successful');
            	window.alert('Please saved the following key in a safe place!\n' + response.key);
        	}
    	});	
	}
    
    function login() {

    	// Get username and password from input fields
    	var username = $('username').value;
    	var password = $('password').value;
    	var req = JSON.stringify({});

	    // TODO: Display loading message
	    
    	// The request parameters
    	var url = './Login';
    	var params = 'username=' + encodeURIComponent(username) + '&password=' + encodeURIComponent(password);

	    // Make AJAX call
    	ajax('POST', url + '?' + params, req, function(responseText) {

        	// Parse JSON response
        	var response = JSON.parse(responseText);

	        // Check status from server response
    	    if (response.status === 'OK') {
        	    // Login successful, redirect or perform actions as needed
            	console.log('Login successful');
            	
		        $('welcome-msg').textContent = 'Welcome ' + response.name;
            	hideElement($('login-form'));
            	showElement($('logout-link'));
            	showElement($('item-list'));
            	loadMachines();
            	showElement($('menu'));
            	resetFormValue();
        	}
    	}, function() {
        	// Handle AJAX error
        	console.error('AJAX request failed');
        	// Display error message
        	$('login-error').innerHTML = 'Incorrect username or password';
    	});
	}
	
	function createAccount() {
    	console.log('create Account');

    	// Get username and password from input fields
    	var username = $('username-create').value;
    	var password = $('password-create').value;
    	var fname = $('fname').value;
    	var lname = $('lname').value;
    	var key = $('key-create').value;
    	var req = JSON.stringify({});

    	// The request parameters
    	var url = './CreateAccount';
    	var params = 'username=' + encodeURIComponent(username) + '&password=' + encodeURIComponent(password)
    	+ '&fname=' + encodeURIComponent(fname) + '&lname=' + encodeURIComponent(lname) + '&key=' + encodeURIComponent(key);

	    // TODO: Display loading message

	    // Make AJAX call
    	ajax('POST', url + '?' + params, req, function(responseText) {

        	// Parse JSON response
        	var response = JSON.parse(responseText);

	        // Check status from server response
    	    if (response.status === 'OK') {
        	    // Login successful, redirect or perform actions as needed
            	console.log('Create Account successful, Logged you in automatically');
            	
		        $('welcome-msg').textContent = 'Welcome ' + response.name;
            	hideElement($('login-form'));
            	hideElement($('create-form'));
            	showElement($('item-list'));
            	showElement($('logout-link'));
            	loadMachines();
            	showElement($('menu'));
            	resetFormValue();
        	} else {
            	// Login failed, display error message
            	console.log('Create Account failed');
            	$('create-error').innerHTML = 'Please leave no blanks, or username already exist, or key incorrect';
        	}
    	}, function() {
        	// Handle AJAX error
        	console.error('AJAX request failed');
        	// Display error message
        	$('create-error').innerHTML = 'Please leave no blanks, or username already exist, or key incorrect';
    	});
	}
	
	function logout() {

    	// Get username and password from input fields
    	var req = JSON.stringify({});
	    
    	// The request parameters
    	var url = './Logout';

	    // Make AJAX call
    	ajax('GET', url, req, function(_) {
			console.log("logout");

        	// Login successful, redirect or perform actions as needed
          	console.log('Logout successful');
            	
           	$('welcome-msg').textContent = '';
            hideElement($('item-list'));
            hideElement($('create-form'));
            hideElement($('add-form'));
            hideElement($('menu'));
            hideElement($('logout-link'));
            hideElement($('logDropDown'));
            showElement($('login-form'));
            resetFormValue();
    	}, function() {
        	// Handle AJAX error
        	console.error('AJAX request failed');
        	// Display error message
        	$('logout-link').innerHTML = 'Logout Failed';
    	});
	}
	
    function toggleMachine(item_id) {
        // Check whether this item has been visited or not
        var toggleIcon = $('toggle-icon-' + item_id);

        // The request parameters
        var url = './Toggle';
        var param = 'ip=' + item_id;
        var req = JSON.stringify({});

        ajax('PUT', url + '?' + param, req,
            // successful callback
            function(res) {
                var result = JSON.parse(res);
                if (result.Toggle === 'SUCCESS') {
                    toggleIcon.className = toggleIcon.className ==  'fa fa-toggle-off' ? 'fa fa-toggle-on' : 'fa fa-toggle-off';
                }
         	});
    }
    
    function removeMachine(item_id) {
        // Check whether this item has been visited or not
        var li = $('item-' + item_id);
        li.remove();

        // The request parameters
        var url = './Remove';
        var param = 'ip=' + item_id;
        var req = JSON.stringify({});

        ajax('DELETE', url + '?' + param, req,
            // successful callback
            function(res) {
                var result = JSON.parse(res);
                if (result.Toggle === 'SUCCESS') {
					//alert('Remove Successfully');
                }
            },
            function() {
				showErrorMessage('Remove Failed.');
			});
    }
    
    function addMachine() {
		console.log('Register Machine');

    	// Get username and password from input fields
    	var name = $('name').value;
    	var ip = $('ip').value;
    	var hr = $('hr').value;
    	var min = $('min').value;
    	var req = JSON.stringify({});

    	// The request parameters
    	var url = './Add';
    	var params = 'name=' + encodeURIComponent(name) + '&ip=' + encodeURIComponent(ip)
    	+ '&hr=' + encodeURIComponent(hr) + '&min=' + encodeURIComponent(min);

	    // Make AJAX call
    	ajax('POST', url + '?' + params, req, function(responseText) {

        	// Parse JSON response
        	var response = JSON.parse(responseText);

	        // Check status from server response
    	    if (response.register === 'SUCCESS') {
		        $('add-error').innerHTML = 'Register Successfully';
        	} else {
            	console.log('Add Machine failed');
            	$('add-error').innerHTML = 'IP address already exist.';
        	}
    	}, function() {
        	    console.log('Add Machine failed');
            	$('add-error').innerHTML = 'IP address already exist.';
    	});
	}
	
	function loadMachines() {
        console.log('loadMachine');

        // The request parameters
        var url = './Load';
        var req = JSON.stringify({});

        // display loading message
        showLoadingMessage('Loading machines...');		

        // make AJAX call
        ajax('GET', url, req,
            // successful callback
            function(res) {
                var items = JSON.parse(res);
                if (!items || items.length === 0) {
                    showWarningMessage('No machine found.');
                } else {
					items.sort(function(a, b) {
                	var nameA = a.name.toLowerCase(); // Ignore case
                	var nameB = b.name.toLowerCase(); // Ignore case
                	if (nameA < nameB) return -1;
                	if (nameA > nameB) return 1;
                	return 0; // names must be equal
            });
                    listItems(items);
                }
            },
            // failed callback
            function() {
                showErrorMessage('Cannot load machines. Please try again later');
            });
    }
    
    
	function download() {
		var selectElement = $('dateDropDown');
		var selectedOption = selectElement.options[selectElement.selectedIndex];
		var param = selectedOption.textContent;

		var url = './Download';
		var req = JSON.stringify({});
		
		ajax('GET', url + '?date=' + param, req,
            function(res) {
                // Check if the response contains the file data
        		if (res) {
            		// Create a blob from the response data
            		var blob = new Blob([res], { type: 'text/plain' });
            
            		// Create a temporary URL for the blob
            		var url = URL.createObjectURL(blob);
            
            		// Create an anchor element for initiating the download
            		var a = document.createElement('a');
            		a.href = url;
            		a.download = 'logs_' + param + '.txt'; // Set the filename
            
            		// Trigger a click event on the anchor element to initiate the download
            		a.click();
            
            		// Release the temporary URL
            		URL.revokeObjectURL(url);
            	}
            },
            // failed callback
            function() {
                window.alert("Log not exists.");
            });
	}  
    
    
    /**
     * List items
     * 
     * @param items -
     *            An array of item JSON objects
     */
    function listItems(items) {
        // Clear the current results
        var itemList = $('item-list');
        itemList.innerHTML = '';

        for (var i = 0; i < items.length; i++) {
            addItem(itemList, items[i]);
        }
    }

    /**
     * Add item to the list
     * 
     * @param itemList -
     *            The
     *            <ul id="item-list">
     *            tag
     * @param item -
     *            The item data (JSON object)
     */
    function addItem(itemList, item) {
        var item_id = item.ip;

        // create the <li> tag and specify the id and class attributes
        var li = $('li', {
            id: 'item-' + item_id,
            className: 'item'
        });


        // section
        var section = $('div', {});

        // name
        var title = $('a', {
            target: '_blank',
            className: 'item-name'
        });
        title.innerHTML = item.name;
        section.appendChild(title);

        // ip
        var ip = $('p', {
            className: 'item-ip'
        });
        ip.innerHTML = item.ip;
        section.appendChild(ip);


        li.appendChild(section);

        // time
        var time = $('p', {
            className: 'item-time'
        });

        time.innerHTML = item.hr + ':' + item.min;
        li.appendChild(time);

        // toggle link
        var toggle = $('p', {
            className: 'action-link',
            id: "toggle-link-" + item_id
        });

        toggle.onclick = function() {
            toggleMachine(item_id);		
        };
		
		
        toggle.appendChild($('i', {
            id: 'toggle-icon-' + item_id,
            className: item.status ? 'fa fa-toggle-on' : 'fa fa-toggle-off'
        }));

        li.appendChild(toggle);
        
        // remove link
        var remove = $('p', {
            className: 'action-link',
            id: "remove-link-" + item_id
        });

        remove.onclick = function() {
            removeMachine(item_id);		
        };
		
		
        remove.appendChild($('i', {
            id: 'remove-icon-' + item_id,
            className: 'fa fa-trash'
        }));

        li.appendChild(remove);

        itemList.appendChild(li);
    }
    
    // Function to populate the dropdown with dates from today until 7 days ago
	function populateDateDropdown() {
  		var dropdown = $('dateDropDown');
  		
  		dropdown.innerHTML = "";

  		// Get today's date
  		var today = new Date();
  
  		// Populate the dropdown with dates from today until 7 days ago
  		for (var i = 0; i < 7; i++) {
    		var date = new Date();
    		date.setDate(today.getDate() - i);
    
    		var option = document.createElement("option");
    		option.text = formatDate(date);
    		option.value = formatDate(date);
    		dropdown.add(option);
  		}
	}

	// Function to format a date as "YYYY-MM-DD"
	function formatDate(date) {
  		var year = date.getFullYear();
  		var month = String(date.getMonth() + 1).padStart(2, "0");
  		var day = String(date.getDate()).padStart(2, "0");
  		return year + "-" + month + "-" + day;
	}

    init();

})();
