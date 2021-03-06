package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    @InitBinder
    public void init(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(itemValidator);
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

//    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        // 검증 로직 추가
        Map<String, String> errors = new HashMap<>();

        // --- 필드 검증 ---
        // 1. itemName이 공백인지 확인한다.
        if (!StringUtils.hasText(item.getItemName())) {
            errors.put("itemName", "상품 이름은 필수입니다.");
        }

        // 2. price와 quantity의 범위를 확인한다.
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            errors.put("price", "가격은 1,000 ~ 1,000,000 까지 허용합니다.");
        }

        if (item.getQuantity() == null || item.getQuantity() > 9999) {
            errors.put("quantity", "수량은 최대 9,999 까지 허용합니다.");
        }

        // 3. 조합 검증을 확인한다. (가격 * 수량 >= 10,000)
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                errors.put("globalError", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice);
            }
        }

        // 4. 검증이 실패하면 다시 등록 폼으로 이동한다.
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "validation/v2/addForm";
        }


        // 5. 검증이 성공하면 상품 상세 조회 폼으로 이동한다.
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {

        // --- 필드 검증 ---
        // 1. itemName이 공백인지 확인한다.
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item",
                    "itemName",
                    item.getItemName(),
                    false,
                    new String[]{"required.item.itemName"},
                    null,
                    "상품 이름은 필수 입니다."));
        }

        // 2. price와 quantity의 범위를 확인한다.
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item",
                    "price",
                    item.getPrice(),
                    false,
                    new String[]{"range.item.price"},
                    null,
                    "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }

        if (item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item",
                    "quantity",
                    item.getQuantity(),
                    false,
                    new String[]{"max.item.quantity"},
                    null,
                    "수량은 최대 9,999 까지 허용합니다."));
        }

        // 3. 조합 검증을 확인한다. (가격 * 수량 >= 10,000)
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item",
                        null,
                        null,
                        "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        // 4. 검증이 실패하면 다시 등록 폼으로 이동한다.
        if (bindingResult.hasErrors()) {
            log.info("BindingResult={}", bindingResult);
            return "validation/v2/addForm";
        }


        // 5. 검증이 성공하면 상품 상세 조회 폼으로 이동한다.
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        // --- 필드 검증 ---
        // 1. itemName이 공백인지 확인한다.
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null, null, "상품 이름은 필수 입니다."));
        }

        // 2. price와 quantity의 범위를 확인한다.
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }

        if (item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, null, null, "수량은 최대 9,999 까지 허용합니다."));
        }

        // 3. 조합 검증을 확인한다. (가격 * 수량 >= 10,000)
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", null, null, "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        // 4. 검증이 실패하면 다시 등록 폼으로 이동한다.
        if (bindingResult.hasErrors()) {
            log.info("BindingResult={}", bindingResult);
            return "validation/v2/addForm";
        }


        // 5. 검증이 성공하면 상품 상세 조회 폼으로 이동한다.
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

////    @PostMapping("/add")
//    public String addItemV3(@ModelAttribute Item item,
//                            BindingResult bindingResult,
//                            RedirectAttributes redirectAttributes) {
//
//        // --- 필드 검증 ---
//        // 1. itemName이 공백인지 확인한다.
//        if (!StringUtils.hasText(item.getItemName())) {
//            bindingResult.addError(new FieldError("item",
//                    "itemName",
//                    item.getItemName(),
//                    false,
//                    new String[]{"required.item.itemName"},
//                    null,
//                    "상품 이름은 필수 입니다."));
//        }
//
//        // 2. price와 quantity의 범위를 확인한다.
//        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
//            bindingResult.addError(new FieldError("item",
//                    "price",
//                    item.getPrice(),
//                    false,
//                    new String[]{"range.item.price"},
//                    new Object[]{1000, 1000000},
//                    "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
//        }
//
//        if (item.getQuantity() == null || item.getQuantity() > 9999) {
//            bindingResult.addError(new FieldError("item",
//                    "quantity",
//                    item.getQuantity(),
//                    false,
//                    new String[]{"max.item.quantity"},
//                    new Object[]{9999},
//                    "수량은 최대 9,999 까지 허용합니다."));
//        }
//
//        // 3. 조합 검증을 확인한다. (가격 * 수량 >= 10,000)
//        if (item.getPrice() != null && item.getQuantity() != null) {
//            int resultPrice = item.getPrice() * item.getQuantity();
//            if (resultPrice < 10000) {
//                bindingResult.addError(new ObjectError("item",
//                        new String[]{"totalPriceMin"},
//                        new Object[]{10000, resultPrice},
//                        "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
//            }
//        }
//
//        // 4. 검증이 실패하면 다시 등록 폼으로 이동한다.
//        if (bindingResult.hasErrors()) {
//            log.info("BindingResult={}", bindingResult);
//            return "validation/v2/addForm";
//        }
//
//
//        // 5. 검증이 성공하면 상품 상세 조회 폼으로 이동한다.
//        Item savedItem = itemRepository.save(item);
//        redirectAttributes.addAttribute("itemId", savedItem.getId());
//        redirectAttributes.addAttribute("status", true);
//        return "redirect:/validation/v2/items/{itemId}";
//    }

//    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        // --- 필드 검증 ---
        // 1. itemName이 공백인지 확인한다.
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName", "required");
        }

        // 2. price와 quantity의 범위를 확인한다.
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }

        if (item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        // 3. 조합 검증을 확인한다. (가격 * 수량 >= 10,000)
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();

            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        // 4. 검증이 실패하면 다시 등록 폼으로 이동한다.
        if (bindingResult.hasErrors()) {
            log.info("BindingResult={}", bindingResult);
            return "validation/v2/addForm";
        }


        // 5. 검증이 성공하면 상품 상세 조회 폼으로 이동한다.
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        itemValidator.validate(item, bindingResult);

        // 4. 검증이 실패하면 다시 등록 폼으로 이동한다.
        if (bindingResult.hasErrors()) {
            log.info("BindingResult={}", bindingResult);
            return "validation/v2/addForm";
        }


        // 5. 검증이 성공하면 상품 상세 조회 폼으로 이동한다.
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute Item item,  // 1, 2, 3 검증 로직이 처리된다.
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        // 4. 검증이 실패하면 다시 등록 폼으로 이동한다.
        if (bindingResult.hasErrors()) {
            log.info("BindingResult={}", bindingResult);
            return "validation/v2/addForm";
        }

        // 5. 검증이 성공하면 상품 상세 조회 폼으로 이동한다.
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

