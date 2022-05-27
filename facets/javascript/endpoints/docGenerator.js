const docGenerator = async (parameters) =>  {
	const baseUrl = window.location.origin;
	const url = new URL(`${window.location.pathname.split('/')[1]}/rest/docGenerator/${parameters.moduleCode}`, baseUrl);
	return fetch(url.toString(), {
		method: 'POST', 
		headers : new Headers({
 			'Content-Type': 'application/json'
		}),
		body: JSON.stringify({
			
		})
	});
}

const docGeneratorForm = (container) => {
	const html = `<form id='docGenerator-form'>
		<div id='docGenerator-moduleCode-form-field'>
			<label for='moduleCode'>moduleCode</label>
			<input type='text' id='docGenerator-moduleCode-param' name='moduleCode'/>
		</div>
		<button type='button'>Test</button>
	</form>`;

	container.insertAdjacentHTML('beforeend', html)

	const moduleCode = container.querySelector('#docGenerator-moduleCode-param');

	container.querySelector('#docGenerator-form button').onclick = () => {
		const params = {
			moduleCode : moduleCode.value !== "" ? moduleCode.value : undefined
		};

		docGenerator(params).then(r => r.text().then(
				t => alert(t)
			));
	};
}

export { docGenerator, docGeneratorForm };