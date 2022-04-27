package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.SaveCheck;
import hello.itemservice.domain.item.UpdateCheck;
import hello.itemservice.web.validation.form.ItemSaveForm;
import hello.itemservice.web.validation.form.ItemUpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v4/items")
@RequiredArgsConstructor
public class ValidationItemControllerV4 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v4/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/item";
    }

    @GetMapping("/add")
    public String addForm2(Model model) {
        model.addAttribute("item", new ItemSaveForm());
        return "validation/v4/addForm";
    }

    @PostMapping("/add")
    public String addItem3(@Validated @ModelAttribute("item") ItemSaveForm itemSaveForm,  // 1, 2, 3 검증 로직이 처리된다.
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {

        String itemName = itemSaveForm.getItemName();
        Integer price = itemSaveForm.getPrice();
        Integer quantity = itemSaveForm.getQuantity();

        if (price != null && quantity != null) {
            int resultPrice = price * quantity;
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        // 4. 검증이 실패하면 다시 등록 폼으로 이동한다.
        if (bindingResult.hasErrors()) {
            log.info("BindingResult={}", bindingResult);
            return "validation/v4/addForm";
        }

        Item newItem = new Item();
        newItem.setItemName(itemName);
        newItem.setPrice(price);
        newItem.setQuantity(quantity);

        // 5. 검증이 성공하면 상품 상세 조회 폼으로 이동한다.
        Item savedItem = itemRepository.save(newItem);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v4/items/{itemId}";
    }

//    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/editForm";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm2(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);

        ItemUpdateForm itemUpdateForm = new ItemUpdateForm();
        itemUpdateForm.setId(item.getId());
        itemUpdateForm.setItemName(item.getItemName());
        itemUpdateForm.setPrice(item.getPrice());
        itemUpdateForm.setQuantity(item.getQuantity());

        model.addAttribute("item", itemUpdateForm);
        return "validation/v4/editForm";
    }

//    @PostMapping("/{itemId}/edit")
    public String edit2(@PathVariable Long itemId,
                       @Validated(UpdateCheck.class) @ModelAttribute Item item,
                       BindingResult bindingResult) {

        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();

            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v4/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v4/items/{itemId}";
    }

    @PostMapping("/{itemId}/edit")
    public String edit3(@PathVariable Long itemId,
                        @Validated @ModelAttribute("item") ItemUpdateForm itemUpdateForm,
                        BindingResult bindingResult) {

        String itemName = itemUpdateForm.getItemName();
        Integer price = itemUpdateForm.getPrice();
        Integer quantity = itemUpdateForm.getQuantity();


        if (price != null && quantity != null) {
            int resultPrice = price * quantity;

            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v4/editForm";
        }

        Item updateItemParam = new Item();

        updateItemParam.setItemName(itemName);
        updateItemParam.setPrice(price);
        updateItemParam.setQuantity(quantity);

        itemRepository.update(itemId, updateItemParam);
        return "redirect:/validation/v4/items/{itemId}";
    }
}

