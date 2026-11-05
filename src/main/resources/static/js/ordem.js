// Apenas interação visual. Dados e regras são validados e salvos pelo servidor Java.
(() => {
  const rows=document.getElementById('itemRows'), choice=document.getElementById('serviceChoice');
  if(!rows)return;
  const money=new Intl.NumberFormat('pt-BR',{style:'currency',currency:'BRL'});
  function update(){let total=0;rows.querySelectorAll('tr').forEach((row,index)=>{
    row.querySelector('.service').name=`itens[${index}].servicoId`;
    row.querySelector('.quantity').name=`itens[${index}].quantidade`;
    row.querySelector('.price').name=`itens[${index}].valorUnitario`;
    const value=Math.round((Number(row.querySelector('.price').value)||0)*100)*(Number(row.querySelector('.quantity').value)||0);
    total+=value;row.querySelector('.subtotal').textContent=money.format(value/100);
  });document.getElementById('orderTotal').textContent=money.format(total/100);}
  document.getElementById('addItem').onclick=()=>{
    const message=document.getElementById('itemMessage');message.textContent='';
    if(!choice.value){message.textContent='Selecione um serviço.';return;}
    if([...rows.querySelectorAll('.service')].some(s=>s.value===choice.value)){message.textContent='Serviço já adicionado. Ajuste a quantidade.';return;}
    if(rows.children.length>=100){message.textContent='Limite de 100 serviços por ordem.';return;}
    const row=document.createElement('tr');
    row.innerHTML='<td><select class="service" required aria-label="Serviço"></select></td><td><input class="quantity" aria-label="Quantidade" type="number" min="1" max="9999" step="1" value="1" required></td><td><input class="price" aria-label="Valor unitário" type="number" min="0" max="9999999999.99" step="0.01" required></td><td class="subtotal"></td><td><button type="button" class="link remove removeItem">Remover</button></td>';
    for(const option of choice.options)if(option.value){const copy=document.createElement('option');copy.value=option.value;copy.textContent=option.textContent;row.querySelector('.service').append(copy);}
    row.querySelector('.service').value=choice.value;row.querySelector('.price').value=choice.selectedOptions[0].dataset.price;
    rows.append(row);update();
  };
  rows.addEventListener('input',update);
  rows.addEventListener('change',event=>{if(event.target.matches('.service')){
    const option=[...choice.options].find(o=>o.value===event.target.value);
    if(option)event.target.closest('tr').querySelector('.price').value=option.dataset.price;
  }update();});
  rows.addEventListener('click',event=>{if(event.target.closest('.removeItem')){event.target.closest('tr').remove();update();}});
  update();
})();
