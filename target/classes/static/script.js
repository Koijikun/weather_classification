function checkFile1(files) {
    console.log(files);

    if (files.length != 1) {
        alert("Bitte genau eine Datei hochladen.")
        return;
    }

    const fileSize = files[0].size / 1024 / 1024; // in MiB
    if (fileSize > 10) {
        alert("Datei zu gross (max. 10Mb)");
        return;
    }

    answerPart.style.visibility = "visible";
    const file = files[0];

    // Preview
    if (file) {
        preview.src = URL.createObjectURL(files[0])
    }

    // Upload
    const formData = new FormData();
    for (const name in files) {
        formData.append("image", files[name]);
    }

    fetch('/analyze32', {
        method: 'POST',
        headers: {
        },
        body: formData
    }).then(
        response => {
            console.log(response)
            response.json().then(function (data) {
                displayAnswer("answer", data); // Display the answer in a nice table
            });
        }
    ).then(
        success => console.log(success)
    ).catch(
        error => console.log(error)
    );

}

function checkFile2(files) {
    console.log(files);

    if (files.length != 1) {
        alert("Bitte genau eine Datei hochladen.")
        return;
    }

    const fileSize = files[0].size / 1024 / 1024; // in MiB
    if (fileSize > 10) {
        alert("Datei zu gross (max. 10Mb)");
        return;
    }

    answerPart2.style.visibility = "visible";
    const file = files[0];

    // Preview
    if (file) {
        preview2.src = URL.createObjectURL(files[0])
    }

    // Upload
    const formData2 = new FormData();
    for (const name in files) {
        formData2.append("image", files[name]);
    }

    fetch('/analyze128', {
        method: 'POST',
        headers: {},
        body: formData2
    }).then(
        response => {
            console.log(response)
            response.json().then(function (data) {
                displayAnswer("answer2", data); // Display the answer in a nice table
            });
        }
    ).then(
        success => console.log(success)
    ).catch(
        error => console.log(error)
    );
}

function displayAnswer(answerId, result) {
    let answerDiv = document.getElementById(answerId);
    answerDiv.innerHTML = "";

    let table = document.createElement("table");
    table.className = "table";
    
    let headerRow = table.insertRow();
    let classNameHeader = document.createElement("th");
    classNameHeader.textContent = "Class Name";
    headerRow.appendChild(classNameHeader);
    let probabilityHeader = document.createElement("th");
    probabilityHeader.textContent = "Probability";
    headerRow.appendChild(probabilityHeader);

    result.forEach(item => {
        let row = table.insertRow();
        let classNameCell = row.insertCell();
        classNameCell.textContent = item.className;
        let probabilityCell = row.insertCell();
        probabilityCell.textContent = item.probability.toFixed(2);
    });

    answerDiv.appendChild(table);
}
